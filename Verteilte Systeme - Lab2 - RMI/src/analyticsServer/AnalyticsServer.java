package analyticsServer;

import java.io.IOException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import analyticsServer.AnalyticsRMIInterface;

public class AnalyticsServer {

	/**
	 * Arg 1: Bindingname for AnalyticsServer
	 */
	static String registryHost = "";
	static String analBind = "";
	static int registryPort = 0;
	
	public static void main(String[] args) {
		if (args.length == 1) {
			analBind = args[0];
			readProperties();
			
			AnalyticsRMIHandler rmiHandler = new AnalyticsRMIHandler();

			try {
				// Create stub
				AnalyticsRMIInterface stub = (AnalyticsRMIInterface) UnicastRemoteObject.exportObject(rmiHandler, registryPort);

				// bind stub to Registry
				Registry registry = LocateRegistry.createRegistry(registryPort);
				registry.rebind(analBind, stub);
			} catch (RemoteException e) {
				System.out.println("Error binding to Registry.");
			}
			
			// To-Do: Implement AnalyticsServer
		
		} else {
			System.out.println("Wrong argument count.");
		}
	}
	
	private static void readProperties() {
		java.io.InputStream is = ClassLoader.getSystemResourceAsStream("registry.properties"); 
		if (is != null) {
			java.util.Properties props = new java.util.Properties();
			try {
				try {
					props.load(is);
				} catch (IOException e) {
					System.out.println("Error handling configuration file.");
				}
				registryHost = props.getProperty("registry.host");
				registryPort = Integer.parseInt(props.getProperty("registry.port"));
				
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			System.err.println("Properties file not found!");
		}
	}
}
