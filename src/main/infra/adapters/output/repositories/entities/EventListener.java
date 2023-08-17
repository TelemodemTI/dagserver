package main.infra.adapters.output.repositories.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import main.application.pojos.EventListenerPOJO;

@Entity
@Table(name="sche_events_listeners")
public class EventListener extends EventListenerPOJO{

	 @Id
	 @Column(name="LISTENER_NAME",nullable=false)
	 private String listenerName;
	 
	 @Column(name = "ONSTART", nullable = true)
	 private String onStart;
	 
	 @Column(name = "ONEND", nullable = true)
	 private String onEnd;
	 
	 @Column(name = "GROUP_NAME", nullable = true)
	 private String groupName;

}
