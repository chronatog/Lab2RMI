package managementClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

// Maybe only import what definitely is needed?
// HAHAHAH, ES GEHT
import billingServer.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ManagementClient {
	// bitte lass es funktionieren
	/**
	 * Arg 0: Bindingname for AnalyticsServer
	 * Arg 1: BindingName for BillingServer
	 */
	static BillingServer billingServer = null;
	static BillingServerSecure billingServerSecure = null;
	static String registryHost = "";
	static int registryPort = 0;

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
			double variablePricePercent = 0.0;
			String userBill = "";
			String filterRegex = "";
			int subscriptionId = 0;

			BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));


			readProperties();


			try {
				// Get Analyticsobject to use functions
				Registry registry = LocateRegistry.getRegistry(registryHost,registryPort);
                                billingServer = (BillingServer) registry.lookup(billBind);
			} catch (Exception e) {
				System.out.println("Can't connect to registry.");
				System.exit(1);
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
					 // Login to Billing Server
                    BillingServerSecure bss;
                    try {
                        bss = billingServer.login(userName, userPwd);
                        billingServerSecure = bss;
                    // Store Secure - object if it worked
                    // Store Secure - object if it worked
                        if(bss != null){
                            System.out.println(userName + " successfully logged in");
                        }
                    } catch (RemoteException ex) {
                        System.out.println("Login failed");
                    }
				} else if (line.equals("!steps") && split.length == 1) {
					// Call Pricing Steps from Billing Server
					String steps;
                    try {
                        steps = billingServerSecure.getPriceSteps().toString();
                        System.out.println(steps);

                    } catch (RemoteException ex) {
                        System.out.println("There are no price steps");
                    }

				} else if (line.startsWith("!addStep") && split.length == 5) {
					startPrice 			= Double.parseDouble(split[1]);
					endPrice   			= Double.parseDouble(split[2]);
					fixedPrice 			= Double.parseDouble(split[3]);
					variablePricePercent            = Double.parseDouble(split[4]);
					try {
                        // Add step to BillingServer
                        billingServerSecure.createPriceStep(startPrice, endPrice, fixedPrice, variablePricePercent);
                    } catch (RemoteException ex) {
                        //Logger.getLogger(ManagementClient.class.getName()).log(Level.SEVERE, null, ex);
                    }

				} else if (line.startsWith("!removeStep") && split.length == 3) {
					startPrice = Double.parseDouble(split[1]);
                    endPrice = Double.parseDouble(split[2]);
                    try {
                        // Call RemoveStep from Billing Server
                        billingServerSecure.deletePriceStep(startPrice, endPrice);
                    } catch (RemoteException ex) {
                        //Logger.getLogger(ManagementClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
				} else if (line.startsWith("!bill") && split.length == 2) {
					userBill = split[1];

					// Call Bill from Billing Server
                    String bill;
                    try {
                        bill = billingServerSecure.getBill(userBill).toString();
                        System.out.println(bill);

                    } catch (RemoteException ex) {
                        //Logger.getLogger(ManagementClient.class.getName()).log(Level.SEVERE, null, ex);
                    }                    
				} else if (line.equals("!logout") && split.length == 1) {
					// Destroy Secure - object, get Login - object
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
}