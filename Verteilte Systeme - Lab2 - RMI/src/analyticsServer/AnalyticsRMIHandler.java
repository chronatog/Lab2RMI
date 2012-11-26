package analyticsServer;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import managementClient.EventListener;

import event.AuctionEvent;
import event.BidEvent;
import event.Event;
import event.EventInterface;
import event.StatisticsEvent;
import event.UserEvent;

public class AnalyticsRMIHandler implements AnalyticsRMIInterface {
	
	Pattern pattern;

	List<Subscription> subscriptionList = Collections.synchronizedList(new ArrayList<Subscription>());
	List<UserEvent> userList = Collections.synchronizedList(new ArrayList<UserEvent>());
	List<AuctionEvent> auctionList = Collections.synchronizedList(new ArrayList<AuctionEvent>());

	// Auction statistics variables
	StatisticsEvent avgAuctionDuration = null;
	StatisticsEvent auctionSucessRatio = null;

	// Bid statistics variables
	StatisticsEvent maxBid = null;
	StatisticsEvent bidsPerMinute = null;

	// User statistics variables
	StatisticsEvent minSessionTime = null;
	StatisticsEvent avgSessionTime = null;
	StatisticsEvent maxSessionTime = null;
	
	int auctionCounter = 0;
	int auctionsSucceded = 0;
	int bidCounter = 0;
	int auctionDurationMultiplicator = 0;
	int sessiontimeAvgMultiplicator = 0;
	
	Timestamp systemStartT = new Timestamp(System.currentTimeMillis());
	long systemStart = systemStartT.getTime();
	
	public String subscribe(EventInterface eventListener, String regex) throws RemoteException {
		// Mgmt clients call this to subscribe to events
		try {
			pattern = Pattern.compile(regex);
		} catch (PatternSyntaxException e) {
			return "Error matching regex";
		}

		Subscription subscription = new Subscription(pattern, eventListener);
		subscriptionList.add(subscription);

		return "Created subscription with ID " + subscription.getId() + " for events using filter " + regex;
	}

	public String unsubscribe(int subscriptionId)  throws RemoteException {
		// Mgmt clients call this to unsubscribe from events
		for (int i = 0; i < subscriptionList.size(); i++) {
			if (subscriptionList.get(i).getId().equals(subscriptionId)) {
				subscriptionList.remove(i);
				return "subscription " + subscriptionId + " terminated";
			}
		}
		// suscribtion can not be found
		return "Error: The Suscription ID couldn't be found!";
	}

	public String test() {
		return "Function call works!";
	}
	public void processEvent(Event event)  throws RemoteException {
		
		if (event instanceof AuctionEvent) {
			processAuctionEvent((AuctionEvent)event);
		}
		if (event instanceof BidEvent) {
			processBidEvent((BidEvent)event);
		}
		if (event instanceof UserEvent) {
			processUserEvent((UserEvent)event);
		}

		List<EventInterface> offlineList = new ArrayList<EventInterface>();
		List<EventInterface> notificationListEvent = new ArrayList<EventInterface>(); 
		Iterator<Subscription> iterator = subscriptionList.iterator();

		while (iterator.hasNext()) {
			Subscription subscription = iterator.next();

			pattern = subscription.getRegex();
			Matcher matcher = pattern.matcher(event.getType());

			EventInterface eventListener = subscription.getEventListener();

			if (matcher.find()) {
				if (!notificationListEvent.isEmpty()) {
					if (!notificationListEvent.contains(eventListener)) {
						System.out.println("EventListener will notify");
						notificationListEvent.add(eventListener);
					} // else ignore -> notification already in list

				// zero eventListeners in the notification List
				} else {
					System.out.println("EventListener will notify");
					notificationListEvent.add(subscription.getEventListener());
				}
			}
		}

		Iterator<EventInterface> notifyer = notificationListEvent.iterator();
		while (notifyer.hasNext()) {
			EventInterface eventListener = null;
			try {
				eventListener = notifyer.next();
				eventListener.processEvent(event);
			} catch (RemoteException e) {
				offlineList.add(eventListener);
			}
		}

		// delete eventlisteners from clients who went offline
		if (!offlineList.isEmpty()) {
			for (EventInterface deleter: offlineList) {
				System.out.println("removing subscription because user went offline");
				subscriptionList.remove(deleter);
			}
		}
		offlineList.clear();
		notificationListEvent.clear();
}

	private void processAuctionEvent(AuctionEvent event) {
		
		if (event.getType().equals("AUCTION_STARTED")) {
			auctionList.add(event);
		}
		
		if (event.getType().equals("AUCTION_ENDED")) {
			if (avgAuctionDuration == null) {
				Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
				long timestamp = currentTimestamp.getTime();
				try {
					avgAuctionDuration = new StatisticsEvent("AUCTION_TIME_AVG", timestamp, 0);
				} catch (Exception e) {
					System.out.println("Error: unknown Event type");
				}
			}
			double avgDuration = (avgAuctionDuration.getValue() * auctionDurationMultiplicator + event.getDuration()) / (++auctionDurationMultiplicator);
			avgAuctionDuration.setValue(avgDuration);

			notifyManagementClients(avgAuctionDuration);

			auctionCounter++;
			if (!event.getAuctionWinner().equals("none")) {
				auctionsSucceded++;
			}
			if (auctionSucessRatio == null) {
				Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
				long timestamp = currentTimestamp.getTime();
				try {
					auctionSucessRatio = new StatisticsEvent(StatisticsEvent.AUCTION_SUCCESS_RATIO, timestamp, 0);
				} catch (Exception e) {
					System.out.println("Error: Wrong Eventtype accured");
				}
			}
			// in percent
			auctionSucessRatio.setValue(100/auctionCounter*auctionsSucceded);

			notifyManagementClients(auctionSucessRatio);
		}
	}

	private void processBidEvent(BidEvent event) {
		if (event.getType().equals("BID_PLACED") || event.getType().equals("BID_OVERBID")) {
			if (maxBid == null) {
				Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
				long timestamp = currentTimestamp.getTime();
				try {
					maxBid = new StatisticsEvent(StatisticsEvent.BID_PRICE_MAX, timestamp, event.getPrice());
				} catch (Exception e) {
					System.out.println("Error: Wrong Eventtype accured");
				}
			}
			if (event.getPrice() > maxBid.getValue()) {
				maxBid.setValue(event.getPrice());
			}
			notifyManagementClients(maxBid);

			// bits per minute
			Timestamp nowTimeStamp = new Timestamp(System.currentTimeMillis());
			long now = nowTimeStamp.getTime();

			// determine minutes since systemstart- but it cannot be < 1
			long differenceInMin = (now - systemStart)/1000/60;
			if ((differenceInMin) < 1) {
				differenceInMin = 1;
			}

			double bpm = (double)++bidCounter / differenceInMin;

			if (bidsPerMinute == null) {
				try {
					bidsPerMinute = new StatisticsEvent(StatisticsEvent.BID_COUNT_PER_MINUTE, now, bpm);
				} catch (Exception e) {
					System.out.println("Error: Wrong Eventtype accured");
				}
			}
			else {
				bidsPerMinute.setValue(bpm);
			}
			notifyManagementClients(bidsPerMinute);
		}

	}

	private void processUserEvent(UserEvent event) {
		if (event.getType().equals("USER_LOGIN")) {
			userList.add(event);
			
			// Test, not sure about this
			notifyManagementClients(event);
			//
		}

		if (event.getType().equals("USER_LOGOUT") || event.getType().equals("USER_DISCONNECTED")) {
			// Test, not sure about this
			notifyManagementClients(event);
			//
			
			// get login event and session time
			UserEvent loginEvent;
			long difference = 0;

			Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
			long timestamp = currentTimestamp.getTime();

			for (int i = 0; i < userList.size(); i++) {
				if (userList.get(i).getUserString().equals(event.getUserString())) {
					loginEvent = userList.get(i);
					difference = event.getTimestamp() - loginEvent.getTimestamp(); // new session time
				}
			}
			if (minSessionTime == null) {
				try {
					minSessionTime = new StatisticsEvent(StatisticsEvent.USER_SESSIONTIME_MIN, timestamp, difference);
					notifyManagementClients(minSessionTime);
				} catch (Exception e) {
					System.out.println("wrong Event Type");
				}
			} else {
				// current session time is less than the min -> new min
				if (minSessionTime.getValue() > difference) {
					try {
						minSessionTime = new StatisticsEvent(StatisticsEvent.USER_SESSIONTIME_MIN, timestamp, difference);
						notifyManagementClients(minSessionTime);
					} catch (Exception e) {
						System.out.println("Wrong event type");
					}
				}
			}

			// first maxEvent
			if (maxSessionTime == null) {
				try {
					maxSessionTime = new StatisticsEvent(StatisticsEvent.USER_SESSIONTIME_MAX, timestamp, difference);
					notifyManagementClients(maxSessionTime);
				} catch (Exception e) {
					System.out.println("Wrong event type");
				}
			} else {
				// current session time is bigger than the max -> new max
				if (maxSessionTime.getValue() > difference) {
					try {
						maxSessionTime= new StatisticsEvent(StatisticsEvent.USER_SESSIONTIME_MAX, timestamp, difference);
						notifyManagementClients(maxSessionTime);
					} catch (Exception e) {
						System.out.println("Wrong event type");
					}
				}
			}

			// set avg
			double oldAvgValue;
			if (avgSessionTime == null) {
				oldAvgValue = 0;
			} else {
				oldAvgValue = avgSessionTime.getValue();
			}

			double newValue = (oldAvgValue * sessiontimeAvgMultiplicator + difference) / (1+sessiontimeAvgMultiplicator);
			sessiontimeAvgMultiplicator++;
			try {
				avgSessionTime = new StatisticsEvent(StatisticsEvent.USER_SESSIONTIME_AVG, timestamp, newValue);
				notifyManagementClients(avgSessionTime);
			} catch (Exception e) {
				System.out.println("Wrong event type");
			}
		}

					
	}
	
	private void notifyManagementClients(Event event) {
		List<EventInterface> deleteList = new ArrayList<EventInterface>();
		List<EventInterface> notificationListForThisEvent = new ArrayList<EventInterface>(); 
		Iterator<Subscription> iterator = subscriptionList.iterator();

		System.out.println("Request to notify management client");
		
		while (iterator.hasNext()) {
			Subscription subscription = iterator.next();

			pattern = subscription.getRegex();
			Matcher matcher = pattern.matcher(event.getType());

			EventInterface eventListener = subscription.getEventListener();

			if (matcher.find()) {
				
				System.out.println("Subscription could be matched!");
				
				if (!notificationListForThisEvent.isEmpty()) {
					if (!notificationListForThisEvent.contains(eventListener)) {
						System.out.println("EventListener will notify");
						notificationListForThisEvent.add(eventListener);
					} 

				// zero eventListeners in the notification List
				} else {
					System.out.println("EventListener will notify");
					notificationListForThisEvent.add(subscription.getEventListener());
				}
			}
		}

		Iterator<EventInterface> notifyer = notificationListForThisEvent.iterator();
		while (notifyer.hasNext()) {
			EventInterface eventListener = null;
			try {
				eventListener = notifyer.next();
				eventListener.processEvent(event);
			} catch (RemoteException e) {
				deleteList.add(eventListener);
			}
		}

		if (!deleteList.isEmpty()) {
			for (EventInterface deleter: deleteList) {
				System.out.println("removing subscription for offline user");
				subscriptionList.remove(deleter);
			}
		}
		deleteList.clear();
		notificationListForThisEvent.clear();
	}

}