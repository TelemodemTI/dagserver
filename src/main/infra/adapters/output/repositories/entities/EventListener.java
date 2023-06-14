package main.infra.adapters.output.repositories.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="sche_events_listeners")
public class EventListener {

	 @Id
	 @Column(name="LISTENER_NAME",nullable=false)
	 private String listenerName;
	 
	 @Column(name = "ONSTART", nullable = true)
	 private String onStart;
	 
	 @Column(name = "ONEND", nullable = true)
	 private String onEnd;
	 
	 @Column(name = "GROUP_NAME", nullable = true)
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
