package event;

public class AuctionEvent extends Event {
	private int auctionId;
	private int duration;
	private String auctionWinner;

	public AuctionEvent(String type, long timestamp, int auctionId) throws Exception {
		if ((type.equals("AUCTION_STARTED") || type.equals("AUCTION_ENDED"))) {
			this.type = type;
			this.timestamp = timestamp;
			this.auctionId = auctionId;
		} else {
			throw new Exception();
		}
	}

	public AuctionEvent(String type, long timestamp, long auctionID, int duration, String auctionWinner) throws Exception {

		if ((type.equals("AUCTION_STARTED") || type.equals("AUCTION_ENDED"))) {
			this.type = type;
			this.timestamp = timestamp;
			this.auctionId = auctionId;
			this.duration = duration;
			this.auctionWinner = auctionWinner;
		} else {
			throw new Exception();
		}
	}

	public long getAuctionId() {
		return auctionId;
	}

	public synchronized void setAuctionID(long auctionID) {
		this.auctionId = auctionId;
	}

	public int getDuration() {
		return duration;
	}

	public synchronized void setDuration(int duration) {
		this.duration = duration;
	}

	public String getAuctionWinner() {
		return auctionWinner;
	}

	public synchronized void setAuctionWinner(String auctionWinner) {
		this.auctionWinner = auctionWinner;
	}

}
