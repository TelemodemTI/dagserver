package main.cl.dagserver.domain.core;

import org.springframework.context.ApplicationEvent;
import main.cl.dagserver.domain.exceptions.DomainException;

public class ExceptionEventLog extends ApplicationEvent {

	private static final long serialVersionUID = 1L;
	private DomainException exception;
	private String message;
	private Object source;
	
	public ExceptionEventLog(Object source, DomainException exception, String message) {
		super(source);
		this.source = source;
		this.exception = exception;
		this.message = message;
	}

	public DomainException getException() {
		return exception;
	}

	public void setException(DomainException exception) {
		this.exception = exception;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getSource() {
		return source;
	}

	public void setSource(Object source) {
		this.source = source;
	}

	
	
	
	

}
