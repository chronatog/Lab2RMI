package event;

public class StatisticsEvent extends Event {
	private double value;
	
	public double getValue() {
		return value;
	}
	public synchronized void setValue(double value) {
		this.value = value;
	}
}
