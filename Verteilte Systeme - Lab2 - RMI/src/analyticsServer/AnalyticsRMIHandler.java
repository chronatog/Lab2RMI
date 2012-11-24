package analyticsServer;

import java.rmi.RemoteException;

import event.Event;
import event.EventInterface;

public class AnalyticsRMIHandler implements AnalyticsRMIInterface {

	public String subscribe(EventInterface eventListener, String eventRegEx) throws RemoteException {
		// Mgmt clients call this to subscribe for events

		// Manage subscriptions

		// int subscriptionID = 0;
		// return "Created subscription with ID " + subscriptionID + " for events using filter " + eventRegEx;
		return "";
	}

	public void unsubscribe(String subscriptionID)  throws RemoteException {
		// Mgmt clients call this to unsubscribe from events

		// Manage subscriptions

		//System.out.println("Subscription " + subscriptionID + " terminated");
	}

	public void processEvent(Event event)  throws RemoteException {
		// Auctionserver (Angabe says BillingServer, makes no sense, verify?) calls this to notify of events
		// For every new event, check all subscriptions regular expressions against its type, if match, notify subscriptions

		// If AUCTION_STARTED track auction, no new events
		// If AUCTION_ENDED => AUCTION_TIME_AVG, AUCTION_SUCCESS_RATIO

		// If USER_LOGIN => track user, USER_SESSIONTIME_MIN, USER_SESSIONTIME_AVG
		// If USER_LOGOUT => USER_SESSIONTIME_MIN, USER_SESSIONTIME_MAX, USER_SESSIONTIME_AVG
		// If USER_DISCONNECTED => USER_SESSIONTIME_MIN, USER_SESSIONTIME_MAX, USER_SESSIONTIME_AVG

		// If BID_PLACED => track bid, BID_PRICE_MAX, BID_COUNT_PER_MINUTE
		// If BID_OVERBID => BID_PRICE_MAX, BID_COUNT_PER_MINUTE
		// If BID_WON => stop tracking bid
	}
}