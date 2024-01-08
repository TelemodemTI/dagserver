package main.cl.dagserver.infra.adapters.input.channels;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import main.cl.dagserver.domain.core.ExceptionEventLog;
import main.cl.dagserver.domain.exceptions.DomainException;

public abstract class InputChannel {

	protected ApplicationEventPublisher eventPublisher;
	protected Map<String,Thread> bindings;
	protected Boolean someCondition = false;
	
	@Autowired
	public InputChannel(ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}
	
	@PostConstruct
	public void listener() {
		this.bindings = new HashMap<>();
		 Thread listenerT = new Thread(() -> {
			 try {
					runForever();
         	} catch (InterruptedException ie) {
         		Thread.currentThread().interrupt();
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
	
	public abstract void runForever() throws Exception;
}
