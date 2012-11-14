package event;

import java.io.Serializable;

public abstract class Event implements Serializable {
	private String id;
	private String type;
	private long timestamp;

	public String getId() {
		return id;
	}
	public synchronized void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public synchronized void setType(String type) {
		this.type = type;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public synchronized void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
