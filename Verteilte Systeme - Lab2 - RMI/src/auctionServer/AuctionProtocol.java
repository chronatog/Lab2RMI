package auctionServer;

/* Should not be needed anymore
import java.net.DatagramPacket;
import java.net.DatagramSocket;
*/
import billingServer.BillingServer;
import billingServer.BillingServerSecure;
import java.net.InetAddress;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuctionProtocol {
	protected String userName;
	static boolean exists;
	
	public String processInput(String command) {
		String[] split = command.split(" ");
		String completeString = "";
		if (command.startsWith("!login ")) {
			userName = command.split(" ")[1];
		} else if (command.equals("!list")) {
			
			// Added synchronized block, is "this" correct?
			synchronized(this) {
				Iterator<?> iter = AuctionServer.auctionDescription.entrySet().iterator();
				
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					completeString = completeString + entry.getKey() + ". '" + entry.getValue() + "' " + AuctionServer.auctionOwner.get(entry.getKey()) + " " + AuctionServer.auctionEndtime.get(entry.getKey()) + " " + AuctionServer.auctionHighestBid.get(entry.getKey()).toString() + " " + AuctionServer.auctionHighestBidder.get(entry.getKey()) + "\n";
				}
			}
			
			return completeString;	
		} else if (command.startsWith("!create ")) {
                    synchronized(AuctionServer.auctionDescription){
                        synchronized(AuctionServer.auctionEndtime){
                            synchronized(AuctionServer.auctionHighestBid){
                               synchronized(AuctionServer.auctionHighestBidder){
                                synchronized(AuctionServer.auctionOwner){
                                    int duration = Integer.parseInt(split[1]);
                                    String fullDescription = "";

                                    for (int i = 2; i < split.length; i++) {
                                            fullDescription += split[i] + " ";
                                    }
                                    fullDescription = fullDescription.substring(0, fullDescription.length() - 1);
                                    int newId = AuctionServer.auctionCounter + 1;

                                    Timestamp original = new Timestamp(System.currentTimeMillis());
                            Calendar cal = Calendar.getInstance();
                            cal.setTimeInMillis(original.getTime());
                            cal.add(Calendar.SECOND, duration);
                            Timestamp timestamp = new Timestamp(cal.getTime().getTime());
                            Timer timer = new Timer("AuctionEnd");
                                    MyTask task = new MyTask(newId);
                                    timer.schedule(task, (Long.parseLong(split[1]) * 1000));

                                    AuctionServer.auctionDescription.put(newId, fullDescription);
                                    AuctionServer.auctionEndtime.put(newId, timestamp);
                                    AuctionServer.auctionHighestBid.put(newId, 0.0);
                                    AuctionServer.auctionHighestBidder.put(newId, "");
                                    AuctionServer.auctionOwner.put(newId, userName);
                                    AuctionServer.auctionCounter += 1;

                                    return "An auction '" + fullDescription + "' with id " + newId + " has been created and will end on " + timestamp + ".";
                                }
                              }
                            }
                          }
                     }
		} else if (command.startsWith("!bid ")) {
                    synchronized(AuctionServer.auctionDescription){
                        synchronized(AuctionServer.auctionEndtime){
                            synchronized(AuctionServer.auctionHighestBid){
                               synchronized(AuctionServer.auctionHighestBidder){
                                synchronized(AuctionServer.auctionOwner){

                                int auctionId = Integer.parseInt(split[1]);
                                exists = false;

                                // Add check for existing auction ID
                                synchronized(this) {
                                        Iterator<?> iter = AuctionServer.auctionDescription.entrySet().iterator();

                                        while (iter.hasNext()) {
                                                Map.Entry entry = (Map.Entry) iter.next();
                                                if (entry.getKey().equals(auctionId)) {
                                                        exists = true;
                                                }
                                        }
                                }

                                if (exists == true) {
                                        double oldValue = AuctionServer.auctionHighestBid.get(auctionId);
                                        double bidValue = Double.parseDouble(split[2]);
                                        String oldUser = AuctionServer.auctionHighestBidder.get(auctionId);

                                        if (bidValue > AuctionServer.auctionHighestBid.get(auctionId)) {
                                                // New bid must be higher than last winning bid
                                                if (userName != AuctionServer.auctionHighestBidder.get(auctionId)) {
                                                        // Users can't overbid themselves
                                                        //if (userName != AuctionServer.auctionOwner.get(auctionId)) {
                                                                // Auction Owners can't bid on their auctions
                                                                AuctionServer.auctionHighestBid.put(auctionId, bidValue);
                                                                AuctionServer.auctionHighestBidder.put(auctionId, userName);

                                                                /* Should be removed, no need to notify old highest bidder
                                                                if (oldValue != 0.0) {
                                                                        // Notify old bidder if there is one
                                                                        String overbidString = "!new-bid " + AuctionServer.auctionDescription.get(auctionId);
                                                                        if (AuctionServer.userHostnames.containsKey(oldUser)) {
                                                                                // Notify User directly if he is logged in
                                                                                notifyClient(overbidString, oldUser);
                                                                } else {
                                                                        if (AuctionServer.userMissed.get(oldUser).equals("")) {
                                                                                // If notify - string is empty
                                                                                AuctionServer.userMissed.put(oldUser, overbidString);
                                                                        } else {
                                                                                // If notify - String non-empty
                                                                                AuctionServer.userMissed.put(oldUser, AuctionServer.userMissed.get(oldUser) + ";" + overbidString);
                                                                        }
                                                                }
                                                                }
                                                                */

                                                                return "You successfully bid with " + bidValue + " on '" + AuctionServer.auctionDescription.get(auctionId) + "'.";
                                                        //} else {
                                                        //	return "You can't bid on your own auctions.";
                                                        //}
                                                } else {
                                                        return "You can't overbid yourself.";
                                                }
                                        } else {
                                                return "Bid must be higher than current highest bid.";
                                        }
                                } else {
                                        return "Auction doesn't exist.";
                                }
                                    }
                                }
                               }
                            }
                        }
	}//end bid
	return "";
	}
	/* Should not be needed anymore since clients are not notified by UDP anymore (or notified at all?)
	protected static void notifyClient(String message, String name) {
		byte[] buf = message.getBytes();
		try {
			if (AuctionServer.userHostnames.containsKey(name)) {
				InetAddress address = InetAddress.getByName(AuctionServer.userHostnames.get(name));
	    		
	    		DatagramPacket testPacket = new DatagramPacket(buf, buf.length, address, AuctionServer.userPorts.get(name));
	    		DatagramSocket dsocket = new DatagramSocket();
	    	    dsocket.send(testPacket);
	    	    dsocket.close();
	    		
	    	    InetAddress hostAddress = InetAddress.getByName(AuctionServer.userHostnames.get(name));
			} else {
				//maybe usersMissed needs to be initialized with ""?
				AuctionServer.userMissed.get(name).concat(message + "\n");
			}
		} catch (Exception e) {
			System.out.println("Error sending Test UDP packet.");
		}
	}
	*/
    		
	}
	class MyTask extends TimerTask {
	 private int id;
         static BillingServer billingServer = null;
         static BillingServerSecure billingServerSecure = null;
	    public MyTask(int newId) {
	    	id = newId;
	    	String internString;
		}

	    public void run() {
	    
	    	String highestBidder = AuctionServer.auctionHighestBidder.get(id);
	    	String auctionOwner = AuctionServer.auctionOwner.get(id);
	    	Double highestBid = AuctionServer.auctionHighestBid.get(id);
                long auctionID = id;

                Registry registry;
                try {
                    registry = LocateRegistry.getRegistry("localhost", 11319);

                    billingServer = (BillingServer) registry.lookup("BillingServerRef");

                    BillingServerSecure bss =  billingServer.login("auctionServer", "auctionServer123");
                    billingServerSecure = bss;

                    bss.billAuction(highestBidder, auctionID, highestBid);


                //BillingServerSecure bss;

                } catch (NotBoundException ex) {
                    //Logger.getLogger(ManagementClient.class.getName()).log(Level.SEVERE, null, ex);
                } catch (AccessException ex) {
                   // Logger.getLogger(ManagementClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (RemoteException ex) {
                //Logger.getLogger(ManagementClient.class.getName()).log(Level.SEVERE, null, ex);
                 }
	    	
	    	/* Should be removed, since this is only logic for notifying auction winner and owner
    		
    		String winString = "!auction-ended " + highestBidder + " " + highestBid.toString() + " " + AuctionServer.auctionDescription.get(id);
	    	//notify winner and owner of finished auction if they are online, otherwise queue notifications
	    	
    		if (AuctionServer.userHostnames.containsKey(highestBidder)) {		
	    		AuctionProtocol.notifyClient(winString, highestBidder);
	    	} else {

	    		if (highestBid != 0.0) {		// If an offer was made aka if there is a highest bidder
		    		
		    		if (AuctionServer.userMissed.get(highestBidder).equals("")) {			// If notify - string is empty
		    			AuctionServer.userMissed.put(highestBidder, winString);			// Put winString
		    		} else {														// If notify - String non-empty
		    			AuctionServer.userMissed.put(highestBidder, AuctionServer.userMissed.get(highestBidder) + ";" + winString);
		    		}
	    		}
	    	}
	    	if (AuctionServer.userHostnames.containsKey(auctionOwner)) {
	    		AuctionProtocol.notifyClient(winString, AuctionServer.auctionOwner.get(id));
	    	} else {
	    		if (AuctionServer.userMissed.get(auctionOwner).equals("")) {		// If notify - string is empty
	    			AuctionServer.userMissed.put(auctionOwner, winString);			// Put winString
	    		} else {		// If notify - String non-empty
	    			AuctionServer.userMissed.put(auctionOwner, AuctionServer.userMissed.get(auctionOwner) + ";" + winString);
	    		}
	    	}
	    	
	    	*/
	    	//remove auction from system
	    	AuctionServer.auctionDescription.remove(id);
	    	AuctionServer.auctionEndtime.remove(id);
	    	AuctionServer.auctionHighestBid.remove(id);
	    	AuctionServer.auctionHighestBidder.remove(id);
	    	AuctionServer.auctionOwner.remove(id);
	    	this.cancel();
	    }
	}