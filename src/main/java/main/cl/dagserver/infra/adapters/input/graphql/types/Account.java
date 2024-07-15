package main.cl.dagserver.infra.adapters.input.graphql.types;

import main.cl.dagserver.domain.enums.AccountType;

public class Account {

	private Integer id;
    private String username;
    private AccountType typeAccount;
    
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public AccountType getTypeAccount() {
		return typeAccount;
	}
	public void setTypeAccount(AccountType typeAccount) {
		this.typeAccount = typeAccount;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
}
