package auctionServer;

import java.net.*;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Timestamp;
import java.io.*;

import event.UserEvent;

import analyticsServer.AnalyticsRMIHandler;
import analyticsServer.AnalyticsRMIInterface;
import billingServer.BillingServer;
import billingServer.BillingServerSecure;

import managementClient.EventListener;
import managementClient.EventListenerInterface;

public class ServerThread extends Thread {
	private Socket socket = null;
	/* Should not be needed
	public int udpPort;
	*/
	public String userName;
	public boolean loggedIn;
	protected String analName;
	protected String billName;
	protected BillingServer billingServerHandler;
	protected BillingServerSecure billingServerSecureHandler;
	protected AnalyticsRMIInterface analyticsHandler;
	Registry registry = null;
	protected static String registryHost;
	protected static int registryPort;
	
	public ServerThread(Socket socket, String analName, String billName) {
		super("ServerThread");
		this.socket = socket;
		this.analName = analName;
		this.billName = billName;
	}

	public void run() {
	    try {
	        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        String inputLine, outputLine;
	        inputLine = "";
	        AuctionProtocol auctionP = new AuctionProtocol();
	        
	        while (true) {
	        	try {
	        		
	        		readProperties();
	        		
	        		// Connect to registry
	    			try {

	    				registry = LocateRegistry.getRegistry(registryHost,registryPort);
	    			} catch (Exception e) {
	    				System.out.println("Couldn't find registry.");
	    			}
	    			// Bind Analytics remote object
	        		try {
	        			analyticsHandler = (AnalyticsRMIInterface) registry.lookup(analName);
	        		} catch (AccessException e) {
	        			System.out.println("Couldn't access registry");
	        		} catch (RemoteException e) {
	        			System.out.println("Couldn't connect to Analytics Server");
	        		} catch (NotBoundException e) {
	        			System.out.println("Analytics Server not bound to the registry");
	        		}
	                /*
	        		// Bind Billing remote object
	        		try {
	        			billingServerHandler = (BillingServer) registry.lookup(billName);
	        			this.billingServerLogin();
	        		} catch (AccessException e) {
	        			System.out.println("Couldn't access registry");
	        		} catch (RemoteException e) {
	        			System.out.println("Couldn't connect to Billing Server");
	        		} catch (NotBoundException e) {
	        			System.out.println("Billing Server not bound to the registry");
	        		}
	        		*/
					if (userName == null) {
						out.print(">");
					} else {
						out.print(userName + ">");
					}
	        		inputLine = in.readLine();
				} catch (IOException e) {
					// Close ressources?
					System.exit(-1);
				}
	        	if (inputLine.startsWith("!login")) {
	        		if (!loggedIn) {
	        			userName = inputLine.split(" ")[1];
	        			/* UDP port should not be passed on with !login command
	        			udpPort = Integer.parseInt(inputLine.split(" ")[2]);
	        			*/
	        			
		        		if (!AuctionServer.userHostnames.containsKey(userName)) {
		        			loggedIn = true;
		        			
		        			AuctionServer.userHostnames.put(userName, socket.getInetAddress().getHostAddress());

		        			// Call ProcessEvent from AnalyticsHandler for LOGIN Event
		        			Timestamp logoutTimestamp = new Timestamp(System.currentTimeMillis());
		        			long timestamp = logoutTimestamp.getTime();
		        			try {
		        				analyticsHandler.processEvent(new UserEvent("USER_LOGIN", timestamp, userName));
		        			} catch (RemoteException e) {
		        				System.out.println("Couldn't connect to Analytics Server");
		        			} catch (Exception e) {
		        				System.out.println("Error processing event");
		        			}
		        			
		        			out.println("Successfully logged in as " + userName + "!");
		        			auctionP.processInput(inputLine);
		        		} else {
		        			out.println("User is already logged in!");
		        		}
		        	} else {
	        			out.println("You are already logged in, please log out first.");
	        		}
	        	} else if (inputLine.equals("!logout")) {
	        		if (loggedIn) {
	        			loggedIn = false;
	        			AuctionServer.userHostnames.remove(userName);
	        			
	        			// Call ProcessEvent from AnalyticsHandler for LOGOUT Event
	        			Timestamp logoutTimestamp = new Timestamp(System.currentTimeMillis());
	        			long timestamp = logoutTimestamp.getTime();
        				try {
							analyticsHandler.processEvent(new UserEvent("USER_LOGOUT", timestamp, userName));
						} catch (RemoteException e) {
	        				System.out.println("Couldn't connect to Analytics Server");
	        			} catch (Exception e) {
	        				System.out.println("Error processing event");
	        			}
        				
	        			out.println("Successfully logged out as " + userName + "!");
		        		
		        		userName = null;
	        		} else {
	        			out.println("You have to log in first!");
	        		}
	        	} else if (inputLine.equals("!end")) {
	        		break;	
	        	} else {
	        		if (loggedIn) {
	        			if (inputLine.startsWith("!create ") || inputLine.startsWith("!bid ") || inputLine.equals("!list")) {
		        			outputLine = auctionP.processInput(inputLine);
                            //System.out.println(outputLine);
			    	        out.println(outputLine);
		        		} else {
		        			out.println("Unrecognized command.");
		        		}
	        		} else {
	        			out.println("You must be logged in for this command to work.");
	        		}
	        	}
	        }
	        out.close();
        	in.close();
        	socket.close();
	        
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    }
	private void answerClient(String message) {
		try {
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			out.println(message);
		} catch (Exception e) {
			System.out.println("Error answering client!");
		}
	}
	private void billingServerLogin() {
        if (billingServerHandler != null) {
            try {
                billingServerSecureHandler = billingServerHandler.login("auctionserver", "supersecure");
                if (billingServerSecureHandler == null) {
                    System.out.println("Login to billing server failed");
                }
            } catch (RemoteException ex) {
                System.out.println("Billing Server login Remote Exception");
            }
        } else {
            System.out.println("Not connected to the billing server"); // log
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