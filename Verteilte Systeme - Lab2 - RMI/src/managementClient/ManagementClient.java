package managementClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import analyticsServer.AnalyticsServer;
import billingServer.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import sun.rmi.log.LogOutputStream;

public class ManagementClient {
	/**
	 * Arg 0: Bindingname for AnalyticsServer
	 * Arg 1: BindingName for BillingServer
	 */	
	static String registryHost = "";
	static int registryPort = 0;
	static BillingServer billingServer = null;
	static BillingServerSecure billingServerSecure = null;
	static AnalyticsServer analyticsServer = null;

	public static void main(String[] args) {
		if (args.length == 2) {
			String analBind = args[0];;
			String billBind = args[1];
			String line = "";
			String userName = "";
			String userPwd = "";
			double startPrice = 0.0;
			double endPrice = 0.0;
			double fixedPrice = 0.0;
			double variablePrice = 0.0;
			String userBill = "";
			String filterRegex = "";
			int subscriptionId = 0;
			Registry registry = null;
		
			BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

			readProperties();

			// Connect to registry
			try {

				registry = LocateRegistry.getRegistry(registryHost,registryPort);
			} catch (Exception e) {
				System.out.println("Couldn't find registry.");
			}
			// get billing remote object
			try {
				billingServer = (BillingServer) registry.lookup(billBind);
			} catch (AccessException e1) {
				System.out.println("Access to registry denied.");
			} catch (NotBoundException e1) {
				System.out.println("Analytic Server not found.");
			} catch (RemoteException e1) {
				System.out.println("Problem finding remote object.");
			}

			// get analytics remote object
			try {
				analyticsServer = (AnalyticsServer) registry.lookup(analBind);
			} catch (AccessException e1) {
				System.out.println("Access to registry denied.");
			} catch (NotBoundException e1) {
				System.out.println("Analytic Server not found.");
			} catch (RemoteException e1) {
				System.out.println("Problem finding remote object.");
			} 
			
			while (true) {
				try {
					System.out.print(userName + "> ");
					line = stdin.readLine();
				} catch (IOException e) {
					// Close ressources?
					System.exit(-1);
				}
				String[] split = line.split(" ");

				/*
				* Start of Billing commands
				*/

				if (line.startsWith("!login ") && split.length == 3) {
					userName = split[1];
					userPwd = split[2];
					login(userName, userPwd);
				
				} else if (line.equals("!steps") && split.length == 1) {
					steps();
				
				} else if (line.startsWith("!addStep") && split.length == 5) {
					startPrice 			= Double.parseDouble(split[1]);
					endPrice   			= Double.parseDouble(split[2]);
					fixedPrice 			= Double.parseDouble(split[3]);
					variablePrice 		= Double.parseDouble(split[4]);
					addPriceStep(startPrice, endPrice, fixedPrice, variablePrice);
				
				} else if (line.startsWith("!removeStep") && split.length == 3) {
					startPrice = Double.parseDouble(split[1]);
                    endPrice = Double.parseDouble(split[2]);
                    removeStep(startPrice, endPrice);
				
				} else if (line.startsWith("!bill") && split.length == 2) {
					userBill = split[1];
					bill(userBill);              
				
				} else if (line.equals("!logout") && split.length == 1) {
					logout();
				
					
				/*
				* Start of Analytics commands
				*/

				} else if (line.equals("!subscribe") && split.length == 2) {
					filterRegex = split[1];
					// Subscribe to AnalyticsServer
				} else if (line.equals("!unsubscribe") && split.length == 2) {
					subscriptionId = Integer.parseInt(split[1]);
					// Unsubscribe to AnalyticsServer
				} else if (line.equals("!auto") && split.length == 1) {

					// Set mode to Auto
				} else if (line.equals("!hide") && split.length == 1) {

					// Set mode to Hide
				} else if (line.equals("!print") && split.length == 1) {

					// Print all buffered events
				} else {
					System.out.println("Command not recognized.");
				}
			}
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
	private static void login(String username, String pw) {
		if (billingServer == null) {
			System.out.println("ERROR: Not connected to billing server");
		} else {
			try {
				BillingServerSecure bss = billingServer.login(username, pw);
				if (bss == null) {
					System.out.println("ERROR: Login failed");
				} else {
					billingServerSecure = bss;
					System.out.println(username + " successfully logged in");
				}
			} catch (RemoteException ex) {
				System.out.println("BillingServer Remote Exception");
			}
		}
	}

	private static void logout() {
		billingServerSecure = null;
	}

	private static void addPriceStep(double startPrice, double endPrice, double fixedFee, double variableFee) {
		if (billingServerSecure == null) {
			System.out.println("ERROR: You are currently not logged in");
		} else if (endPrice != 0 && startPrice >= endPrice) {
			System.out.println("ERROR: Incorrect price range");
		} else {
			try {
				billingServerSecure.createPriceStep(startPrice, endPrice, fixedFee, variableFee);
				System.out.println("Step [" + startPrice + " " + (endPrice == 0 ? "INFINITY" : endPrice) + "] successfully added");
			} catch (RemoteException e) {
				// Check if this displays the Error Messages from the billing Server
				System.out.println(e.getMessage());
			}
		}
	}

	private static void removeStep(double startPrice, double endPrice) {
		if (billingServerSecure == null) {
			System.out.println("ERROR: You are currently not logged in");
		} else {
			try {
				billingServerSecure.deletePriceStep(startPrice, endPrice);
				System.out.println("Step [" + startPrice + " " + (endPrice == 0 ? "INFINITY" : endPrice) + "] successfully removed");
			} catch (RemoteException e) {
				System.out.println(e.getMessage());
			} 
		}
	}

	private static void steps() {
		if (billingServerSecure == null) {
			System.out.println("ERROR: You are currently not logged in");
		} else {
			try {
				PriceSteps priceSteps = billingServerSecure.getPriceSteps();
				System.out.println(priceSteps);
			} catch (RemoteException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	private static void bill(String userName) {
		if (billingServerSecure == null) {
			System.out.println("ERROR: You are currently not logged in");
		} else {
			try {
				Bill bill = billingServerSecure.getBill(userName);
				if (bill == null) {
					System.out.println("ERROR: Bill not found for user " + userName);
				} else {
					System.out.println(bill);
				}
			} catch (RemoteException e) {
				System.out.println("Billing Server Remote Exception");
			}
		}
	}
}