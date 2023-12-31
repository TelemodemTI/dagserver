package main.cl.dagserver.domain.enums;

public enum OperatorStatus {
	EXECUTING("EXECUTING"),
	SKIPPED("SKIPPED"),
	OK("OK"),
	ERROR("ERROR"),
	ANY("ANY");
	
	private final String name;       

    private OperatorStatus(String s) {
        name = s;
    }
    @Override
    public String toString() {
        return this.name;
    }
}
