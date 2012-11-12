package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class AuctionProtocol {
	protected String userName;
	public String processInput(String command) {
		String[] split = command.split(" ");
		String completeString = "";
		if (command.startsWith("!login ")) {
			userName = command.split(" ")[1];
		} else if (command.equals("!list")) {
			
			// Added synchronized block, is "this" correct?
			synchronized(this) {
				Iterator<?> iter = Server.auctionDescription.entrySet().iterator();
				
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					completeString = completeString + entry.getKey() + ". '" + entry.getValue() + "' " + Server.auctionOwner.get(entry.getKey()) + " " + Server.auctionEndtime.get(entry.getKey()) + " " + Server.auctionHighestBid.get(entry.getKey()).toString() + " " + Server.auctionHighestBidder.get(entry.getKey()) + "\n";
				}
			}
			
			return completeString;	
		} else if (command.startsWith("!create ")) {
			int duration = Integer.parseInt(split[1]);
			String fullDescription = "";
			
			for (int i = 2; i < split.length; i++) {
				fullDescription += split[i] + " ";
			}
			fullDescription = fullDescription.substring(0, fullDescription.length() - 1);
			int newId = Server.auctionCounter + 1;
			
			Timestamp original = new Timestamp(System.currentTimeMillis());
	        Calendar cal = Calendar.getInstance();
	        cal.setTimeInMillis(original.getTime());
	        cal.add(Calendar.SECOND, duration);
	        Timestamp timestamp = new Timestamp(cal.getTime().getTime());
	        Timer timer = new Timer("AuctionEnd");
			MyTask task = new MyTask(newId);
			timer.schedule(task, (Long.parseLong(split[1]) * 1000));
			
			Server.auctionDescription.put(newId, fullDescription);
			Server.auctionEndtime.put(newId, timestamp);
			Server.auctionHighestBid.put(newId, 0.0);
			Server.auctionHighestBidder.put(newId, "");
			Server.auctionOwner.put(newId, userName);
			Server.auctionCounter += 1;
			
			return "An auction '" + fullDescription + "' with id " + newId + " has been created and will end on " + timestamp + ".";
		} else if (command.startsWith("!bid ")) {
			int auctionId = Integer.parseInt(split[1]);
			double oldValue = Server.auctionHighestBid.get(auctionId);
			double bidValue = Double.parseDouble(split[2]);
			String oldUser = Server.auctionHighestBidder.get(auctionId);

			if (bidValue > Server.auctionHighestBid.get(auctionId)) {						
				// New bid must be higher than last winning bid
				if (userName != Server.auctionHighestBidder.get(auctionId)) {				
					// Users can't overbid themselves
					if (userName != Server.auctionOwner.get(auctionId)) {					
						// Auction Owners can't bid on their auctions
						Server.auctionHighestBid.put(auctionId, bidValue);
						Server.auctionHighestBidder.put(auctionId, userName);
						
						if (oldValue != 0.0) {
							// Notify old bidder if there is one
							String overbidString = "!new-bid " + Server.auctionDescription.get(auctionId);
							if (Server.userHostnames.containsKey(oldUser)) {		
								// Notify User directly if he is logged in
								notifyClient(overbidString, oldUser);
					    	} else {
					    		if (Server.userMissed.get(oldUser).equals("")) {			
					    			// If notify - string is empty
					    			Server.userMissed.put(oldUser, overbidString);			
					    		} else {														
					    			// If notify - String non-empty
					    			Server.userMissed.put(oldUser, Server.userMissed.get(oldUser) + ";" + overbidString);
					    		}
					    	}
						}
						
						return "You successfully bid with " + bidValue + " on '" + Server.auctionDescription.get(auctionId) + "'.";
					} else {
						return "You can't bid on your own auctions.";
					}
				} else {
					return "You can't overbid yourself.";
				}
			} else {
				return "Bid must be higher than current highest bid.";
			}
	}
	return "";
	}
	protected static void notifyClient(String message, String name) {
		byte[] buf = message.getBytes();
		try {
			if (Server.userHostnames.containsKey(name)) {
				InetAddress address = InetAddress.getByName(Server.userHostnames.get(name));
	    		
	    		DatagramPacket testPacket = new DatagramPacket(buf, buf.length, address, Server.userPorts.get(name));
	    		DatagramSocket dsocket = new DatagramSocket();
	    	    dsocket.send(testPacket);
	    	    dsocket.close();
	    		
	    	    InetAddress hostAddress = InetAddress.getByName(Server.userHostnames.get(name));
			} else {
				//maybe usersMissed needs to be initialized with ""?
				Server.userMissed.get(name).concat(message + "\n");
			}
		} catch (Exception e) {
			System.out.println("Error sending Test UDP packet.");
		}
	}
    		
	}
	class MyTask extends TimerTask {
	 private int id;
	    public MyTask(int newId) {
	    	id = newId;
	    	String internString;
		}

	    public void run() {
	    
	    	String highestBidder = Server.auctionHighestBidder.get(id);
	    	String auctionOwner = Server.auctionOwner.get(id);
	    	Double highestBid = Server.auctionHighestBid.get(id);
	    	
    		String winString = "!auction-ended " + highestBidder + " " + highestBid.toString() + " " + Server.auctionDescription.get(id);
	    	//notify winner and owner of finished auction if they are online, otherwise queue notifications
	    	//If Highest bidder is logged in
	    	
    		if (Server.userHostnames.containsKey(highestBidder)) {		
	    		AuctionProtocol.notifyClient(winString, highestBidder);
	    	} else {

	    		if (highestBid != 0.0) {		// If an offer was made aka if there is a highest bidder
		    		/* DEBUG
	    			System.out.println("Size of userMissed Hash: " + Server.userMissed.size());
		    		System.out.println("userMissed Hash content: " + Server.userMissed.toString());
		    		System.out.println("Notification ready, auctionID: " + id + ", user not logged in: " + highestBidder);
		    		System.out.println("Missed Notifications for User: '" + Server.userMissed.get(highestBidder) + "'");
		    		DEBUG */
		    		
		    		if (Server.userMissed.get(highestBidder).equals("")) {			// If notify - string is empty
		    			Server.userMissed.put(highestBidder, winString);			// Put winString
		    		} else {														// If notify - String non-empty
		    			Server.userMissed.put(highestBidder, Server.userMissed.get(highestBidder) + ";" + winString);
		    		}
	    		}
	    	}
	    	if (Server.userHostnames.containsKey(auctionOwner)) {
	    		AuctionProtocol.notifyClient(winString, Server.auctionOwner.get(id));
	    	} else {
	    		if (Server.userMissed.get(auctionOwner).equals("")) {		// If notify - string is empty
	    			Server.userMissed.put(auctionOwner, winString);			// Put winString
	    		} else {		// If notify - String non-empty
	    			Server.userMissed.put(auctionOwner, Server.userMissed.get(auctionOwner) + ";" + winString);
	    		}
	    	}
	    	//remove auction from system
	    	Server.auctionDescription.remove(id);
	    	Server.auctionEndtime.remove(id);
	    	Server.auctionHighestBid.remove(id);
	    	Server.auctionHighestBidder.remove(id);
	    	Server.auctionOwner.remove(id);
	    	this.cancel();
	    }
	}