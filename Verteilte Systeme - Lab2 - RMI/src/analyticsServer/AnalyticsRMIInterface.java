package analyticsServer;

import java.rmi.Remote;
import java.rmi.RemoteException;

import event.Event;
import event.EventInterface;

public interface AnalyticsRMIInterface extends Remote {

	String subscribe(EventInterface eventListener, String eventRegEx) throws RemoteException;

	void unsubscribe(String suscribtionID)  throws RemoteException ;
	
	void processEvent(Event event)  throws RemoteException ;
}