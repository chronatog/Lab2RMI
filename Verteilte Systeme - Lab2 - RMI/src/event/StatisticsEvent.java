package event;

import java.util.ArrayList;
import java.util.List;

public class StatisticsEvent extends Event {
	private double value;
	List<String> typeList = new ArrayList<String>();

	public static final String USER_SESSIONTIME_MIN = "USER_SESSIONTIME_MIN";
	public static final String USER_SESSIONTIME_MAX = "USER_SESSIONTIME_MAX";
	public static final String USER_SESSIONTIME_AVG = "USER_SESSIONTIME_AVG";
	public static final String BID_PRICE_MAX = "BID_PRICE_MAX";
	public static final String BID_COUNT_PER_MINUTE = "BID_COUNT_PER_MINUTE";
	public static final String AUCTION_TIME_AVG = "AUCTION_TIME_AVG";
	public static final String AUCTION_SUCCESS_RATIO = "AUCTION_SUCCESS_RATIO";

	public double getValue() {
		return value;
	}
	public synchronized void setValue(double value) {
		this.value = value;
	}
	
	public StatisticsEvent(String type, long timestamp, double value) throws Exception {
		typeList.add(USER_SESSIONTIME_MIN);
		typeList.add(USER_SESSIONTIME_MAX);
		typeList.add(USER_SESSIONTIME_AVG);
		typeList.add(BID_PRICE_MAX);
		typeList.add(BID_COUNT_PER_MINUTE);
		typeList.add(AUCTION_TIME_AVG);
		typeList.add(AUCTION_SUCCESS_RATIO);

		// If Type is unknown
		if (!typeList.contains(type)) {
			throw new Exception();
		}
		this.type = type;
		this.timestamp = timestamp;
		this.value = value;
	}
}
