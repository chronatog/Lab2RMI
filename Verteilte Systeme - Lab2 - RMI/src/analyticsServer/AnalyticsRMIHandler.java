package analyticsServer;

import java.rmi.RemoteException;

import event.Event;
import event.EventInterface;

public class AnalyticsRMIHandler implements AnalyticsRMIInterface {

	public String subscribe(EventInterface eventListener, String eventRegEx) throws RemoteException {
		// Mgmt clients call this to subscribe for events
		
		// int subscriptionID = 0;
		// return "Created subscription with ID " + subscriptionID + " for events using filter " + eventRegEx;
		return "";
	}

	public void unsubscribe(String subscriptionID)  throws RemoteException {
		// Mgmt clients call this to unsubscribe from events
		
		//System.out.println("Subscription " + subscriptionID + " terminated");
	}

	public void processEvent(Event event)  throws RemoteException {
		// Auctoinserver calls this to notify of events
	}
}
