package biddingClient;
/* Should not be needed at all
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ClientUdpThread extends Thread {
	private DatagramSocket socket = null;
	
	public ClientUdpThread(DatagramSocket socket) {	
		super("ClientThread");
		this.socket = socket;
	}
	public void run() {
		byte[] buf = new byte[256];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		String fullDescription = "";
		
		while (true) {
			try {
				socket.receive(packet);
				
				String message = new String(buf, 0, packet.getLength());
				String[] split = message.split(" ");
				String command 			= split[0];
				
				if (command.equals("!auction-ended")) {
					fullDescription = "";
					String highestBidder	= split[1];
					Double highestBid  		= Double.parseDouble(split[2]);
					
					//Iterate over auction description part
					for (int i = 3; i < split.length; i++) {
						fullDescription += split[i] + " ";
					}
					fullDescription = fullDescription.substring(0, fullDescription.length() - 1);		
					
					if (BiddingClient.userName.equals(highestBidder)) {	
						// If highest Bidder
						BiddingClient.usage("The auction '" + fullDescription + "' has ended. You won with " + highestBid + "!");
					} else if (highestBid == 0.0){
						// If no bid
						BiddingClient.usage("The auction '" + fullDescription + "' has ended. No bids were made.");
					} else {
						// If owner
						BiddingClient.usage("The auction '" + fullDescription + "' has ended. " + highestBidder + " won with " + highestBid + ".");
					}
				} else if (command.equals("!new-bid")){
						// Iterate over auction description
						for (int i = 1; i < split.length; i++) {
							fullDescription += split[i] + " ";
						}
						fullDescription = fullDescription.substring(0, fullDescription.length() - 1);
						BiddingClient.usage("You have been overbid on '" + fullDescription + "'");
					} else {
						BiddingClient.usage(message);
					}
					packet.setLength(buf.length);
				} catch (IOException e) {
					BiddingClient.usage("Error receiving UDP packet.");
					System.exit(-1);
				}
			}
		}
	}
*/