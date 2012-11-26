package analyticsServer;

import java.util.regex.Pattern;

import event.EventInterface;

public class Subscription {
	private static int idCount = 0;
	private final String id = Subscription.createId();
	private Pattern regex;
	private EventInterface eventListener;

	public Subscription(Pattern regex, EventInterface eventListener) {
		this.regex = regex;
		this.eventListener = eventListener;
	}

	public Pattern getRegex() {
		return regex;
	}

	public synchronized void setRegex(Pattern regex) {
		this.regex = regex;
	}

	public EventInterface getEventListener() {
		return eventListener;
	}

	public synchronized void setEventListener(EventInterface eventListener) {
		this.eventListener = eventListener;
	}

	public String getId() {
		return id;
	}

	private static synchronized String createId() {
		return Integer.toString(idCount+=1);
	}
}
