package managementClient;

import java.rmi.Remote;
import java.rmi.RemoteException;
import event.Event;

public interface EventListenerInterface extends Remote {
	void processEvent(Event event) throws RemoteException;
	int getID() throws RemoteException;
	void setID(int id) throws RemoteException;
}