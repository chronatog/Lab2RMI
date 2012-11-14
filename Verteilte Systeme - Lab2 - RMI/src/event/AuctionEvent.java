package event;

public class AuctionEvent extends Event {
	private String auctionId;
	
	public String getAuctionId() {
		return auctionId;
	}
	public synchronized void setAuctionId(String id) {
		this.auctionId = id;
	}
}
