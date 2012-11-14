package analyticsServer;

import java.rmi.RemoteException;

public class AnalyticsRMIHandler implements AnalyticsRMIInterface {

	public String subscribe(EventInterface eventListener, String eventRegEx) throws RemoteException {
		// int subscriptionID = 0;
		// return "Created subscription with ID " + subscriptionID + " for events using filter " + eventRegEx;
		return "";
	}

	public void unsubscribe(String subscriptionID)  throws RemoteException {
		//System.out.println("Subscription " + subscriptionID + " terminated");
	}

	public void processEvent(Event event)  throws RemoteException {
	}
}
