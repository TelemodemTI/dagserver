package main.cl.dagserver.domain.exceptions;

public class DomainException extends Exception { 
    
	private static final long serialVersionUID = 1L;
	private final String message;

	public DomainException(Exception exception) {
		super(exception);
		this.message = exception.getMessage();
	}
	
	public DomainException(Throwable ex) {
		super(ex.getMessage());
        this.message = ex.getMessage();
	}

	@Override
	public String getMessage() {
		return message;
	}

}