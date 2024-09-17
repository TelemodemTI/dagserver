package main.cl.dagserver.infra.adapters.input.channels;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

public abstract class InputChannel {

	@Autowired
	protected ApplicationEventPublisher eventPublisher;
	protected Map<String,Thread> bindings;
	protected Boolean someCondition = false;
	
	

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
