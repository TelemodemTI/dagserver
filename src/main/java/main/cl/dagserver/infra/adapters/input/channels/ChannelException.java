package main.cl.dagserver.infra.adapters.input.channels;

public class ChannelException extends Exception { 
    
	private static final long serialVersionUID = 1L;
	private final String message;

	public ChannelException(Exception exception) {
		super(exception);
		this.message = exception.getMessage();
	}
	
	public ChannelException(Throwable ex) {
		super(ex.getMessage());
        this.message = ex.getMessage();
	}

	@Override
	public String getMessage() {
		return message;
	}

}