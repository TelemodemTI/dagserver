package main.cl.dagserver.domain.model;


public class EventListenerDTO {
	private String listenerName;
	 private String onStart;
	 private String onEnd;
	 private String groupName;
	 private String tag;
	 private String jarname;
	public String getJarname() {
		return jarname;
	}
	public void setJarname(String jarname) {
		this.jarname = jarname;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
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
