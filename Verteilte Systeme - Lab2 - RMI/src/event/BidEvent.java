package event;

public class BidEvent extends Event {
	private String userName;
	private long auctionId;
	private double price;

	public BidEvent(String type, long timestamp, String userName, long auctionID, double price) throws Exception {
		if ((type.equals("BID_PLACED") || type.equals("BID_OVERBID") || type.equals("BID_WON"))) {
			this.type = type;
			this.timestamp = timestamp;
			this.userName = userName;
			this.auctionId = auctionID;
			this.price = price;
		} else {
			throw new Exception();
		}
	}

	public String getUserName() {
		return userName;
	}

	public synchronized void setUserName(String userName) {
		this.userName = userName;
	}

	public long getAuctionID() {
		return auctionId;
	}

	public synchronized void setAuctionID(long auctionID) {
		this.auctionId = auctionID;
	}

	public double getPrice() {
		return price;
	}

	public synchronized void setPrice(double price) {
		this.price = price;
	}
}
