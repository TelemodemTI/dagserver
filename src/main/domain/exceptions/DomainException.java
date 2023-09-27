package main.domain.exceptions;

import java.util.Objects;

public class DomainException extends Exception { 
    
	private static final long serialVersionUID = 1L;
	private String message;

	public DomainException(String errorMessage) {
        super(errorMessage);
        this.message = errorMessage;
    }
	
	public DomainException(Throwable ex) {
		super(ex.getMessage());
        this.message = ex.getMessage();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}