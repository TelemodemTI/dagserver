package main.cl.dagserver.infra.adapters.output.repositories.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="sche_events_listeners")
public class EventListener{

	 @Id
	 @Column(name="LISTENER_NAME",nullable=false)
	 private String listenerName;
	 
	 @Column(name = "ONSTART", nullable = true)
	 private String onStart;
	 
	 @Column(name = "ONEND", nullable = true)
	 private String onEnd;
	 
	 @Column(name = "GROUP_NAME", nullable = true)
	 private String groupName;
	 
	 @Column(name = "TAG", nullable = true)
	 private String tag;

	 @Column(name = "JARNAME", nullable = true)
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
