package event;

public class UserEvent extends Event {
	private String userString;
	
	public String getUserString() {
		return userString;
	}
	public synchronized void setUserString(String userString) {
		this.userString = userString;
	}
}
