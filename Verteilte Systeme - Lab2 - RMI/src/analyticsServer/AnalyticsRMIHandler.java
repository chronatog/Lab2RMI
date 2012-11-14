package analyticsServer;

import java.rmi.RemoteException;

public class AnalyticsRMIHandler implements AnalyticsRMIInterface {

	public String subscribe(EventInterface eventListener, String eventRegEx) throws RemoteException {
		//return "Created subscription with ID 17 for events using filter '(USER_.*)|(BID_.*)'";
		return "";
	}

	public void unsubscribe(String subscriptionID)  throws RemoteException {
		//System.out.println("Subscription " + subscriptionID + " terminated");
	}

	public void processEvent(Event event)  throws RemoteException {
	}
}
