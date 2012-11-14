package analyticsServer;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface EventInterface extends Remote {
	void processEvent(Event e) throws RemoteException;
}