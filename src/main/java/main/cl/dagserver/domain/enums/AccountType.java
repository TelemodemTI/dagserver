package main.cl.dagserver.domain.enums;

public enum AccountType {
	ADMIN("ADMIN"),
	USER("USER");
	
	private final String name;       

    private AccountType(String s) {
        name = s;
    }
    @Override
    public String toString() {
        return this.name;
    }
}
