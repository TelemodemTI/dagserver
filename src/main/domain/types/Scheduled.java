package main.domain.types;


public class Scheduled {
	private String dagname;
	private String groupname;
	private Long nextFireAt;
	private String eventTrigger;
	public String getDagname() {
		return dagname;
	}
	public void setDagname(String dagname) {
		this.dagname = dagname;
	}
	public String getGroupname() {
		return groupname;
	}
	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}
	public Long getNextFireAt() {
		return nextFireAt;
	}
	public void setNextFireAt(Long nextFireAt) {
		this.nextFireAt = nextFireAt;
	}
	public String getEventTrigger() {
		return eventTrigger;
	}
	public void setEventTrigger(String eventTrigger) {
		this.eventTrigger = eventTrigger;
	}
}
