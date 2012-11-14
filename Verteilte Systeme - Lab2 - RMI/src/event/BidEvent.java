package event;

public class BidEvent extends Event {
	private String userName;
	private long auctionID;
	private double price;
	
	public String getUserName() {
		return userName;
	}
	public synchronized void setUserName(String userName) {
		this.userName = userName;
	}
	public long getAuctionID() {
		return auctionID;
	}
	public synchronized void setAuctionID(long auctionID) {
		this.auctionID = auctionID;
	}
	public double getPrice() {
		return price;
	}
	public synchronized void setPrice(double price) {
		this.price = price;
	}
}
