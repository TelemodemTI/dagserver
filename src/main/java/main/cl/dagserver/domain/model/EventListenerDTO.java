package main.cl.dagserver.domain.model;


public class EventListenerDTO {
	private String listenerName;
	 private String onStart;
	 private String onEnd;
	 private String groupName;
	public String getListenerName() {
		return listenerName;
	}
	public void setListenerName(String listenerName) {
		this.listenerName = listenerName;
	}
	public String getOnStart() {
		return onStart;
	}
	public void setOnStart(String onStart) {
		this.onStart = onStart;
	}
	public String getOnEnd() {
		return onEnd;
	}
	public void setOnEnd(String onEnd) {
		this.onEnd = onEnd;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}	 
}
