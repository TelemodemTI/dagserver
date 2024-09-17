package main.cl.dagserver.infra.adapters.input.channels;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import main.cl.dagserver.domain.core.ExceptionEventLog;
import main.cl.dagserver.domain.exceptions.DomainException;

public abstract class InputChannel2 {

	protected ApplicationEventPublisher eventPublisher;
	protected Map<String,Thread> bindings;
	protected Boolean someCondition = false;
	
	@Autowired
	protected InputChannel2(ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}
	
	//@PostConstruct
	public void listener() {
		this.bindings = new HashMap<>();
		 Thread listenerT = new Thread(() -> {
			 try {
					runForever();
         	} catch (Exception e) {
					eventPublisher.publishEvent(new ExceptionEventLog(this, new DomainException(e), "listenerHandler"));
			} 
		});
		listenerT.start();
	}
	
	public void setBindings(Map<String, Thread> bindings) {
		this.bindings = bindings;
	}
	public void setSomeCondition(Boolean someCondition) {
		this.someCondition = someCondition;
	}
	public void stopListener() {
		this.someCondition = true;
	}
	
	public abstract void runForever() throws ChannelException;
}
