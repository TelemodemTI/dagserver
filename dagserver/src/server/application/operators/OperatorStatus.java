package server.application.operators;

public enum OperatorStatus {
	OK("OK"),
	ERROR("ERROR"),
	ANY("ANY");
	
	private final String name;       

    private OperatorStatus(String s) {
        name = s;
    }
    
    public String toString() {
        return this.name;
    }
}
