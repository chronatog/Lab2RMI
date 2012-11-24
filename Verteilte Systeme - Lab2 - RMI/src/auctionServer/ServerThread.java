package auctionServer;

import java.net.*;
import java.io.*;

public class ServerThread extends Thread {
	private Socket socket = null;
	/* Should not be needed
	public int udpPort;
	*/
	public String userName;
	public boolean loggedIn;
	
	public ServerThread(Socket socket) {
		super("ServerThread");
		this.socket = socket;
	}

	public void run() {
		 
	    try {
	        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        String inputLine, outputLine;

	        AuctionProtocol auctionP = new AuctionProtocol();

	        while ((inputLine = in.readLine()) != null) {
	        	if (inputLine.startsWith("!login")) {
	        		if (!loggedIn) {
	        			userName = inputLine.split(" ")[1];
	        			/* UDP port should not be passed on with !login command
	        			udpPort = Integer.parseInt(inputLine.split(" ")[2]);
	        			*/
	        			
		        		if (!AuctionServer.userHostnames.containsKey(userName)) {
		        			loggedIn = true;
		        			
		        			/* Should not be needed
		        			AuctionServer.userPorts.put(userName, udpPort);
		        			*/
		        			
		        			AuctionServer.userHostnames.put(userName, socket.getInetAddress().getHostAddress());
		        			//System.out.println("user: " + userName);

		        			out.println("Successfully logged in as " + userName + "!");
		        			
		        			/* Should be removed, since no notifications are needed
		        			if (AuctionServer.userMissed.containsKey(userName)) {
		        				String[] notifications = AuctionServer.userMissed.get(userName).split(";");
		        				
		        				if (!notifications[0].equals("")) {
		        					for (int i = 0; i < notifications.length; i++) {
		        						AuctionProtocol.notifyClient(notifications[i], userName);
		        					}
		        				}
		        			} else {
		        				AuctionServer.userMissed.put(userName, "");	
		        			}
		        			*/
		        			
		        			
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
	        			
	        			/* Should not be needed anymore
	        			AuctionServer.userPorts.remove(userName);
		        		udpPort = 0;
		        		*/
	        			
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
}