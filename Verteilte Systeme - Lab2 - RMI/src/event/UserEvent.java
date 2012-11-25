package event;

public class UserEvent extends Event {
	private String userString;
	
	public UserEvent(String type, long timestamp, String userName) throws Exception {
		if ((type.equals("USER_LOGIN") || type.equals("USER_LOGOUT") || type.equals("USER_DISCONNECTED"))) {
			this.type = type;
			this.timestamp = timestamp;
			this.userString = userString;
		} else {
			throw new Exception();
		}
	}
	
	public String getUserString() {
		return userString;
	}
	public synchronized void setUserString(String userString) {
		this.userString = userString;
	}
}
