package main.cl.dagserver.domain.model;

import java.util.Date;

import main.cl.dagserver.domain.enums.AccountType;

public class UserDTO {
	
	private Integer id;
	private String username;
	private String pwdhash;
	private AccountType typeAccount;
	private Date createdAt;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPwdhash() {
		return pwdhash;
	}
	public void setPwdhash(String pwdhash) {
		this.pwdhash = pwdhash;
	}
	public AccountType getTypeAccount() {
		return typeAccount;
	}
	public void setTypeAccount(AccountType typeAccount) {
		this.typeAccount = typeAccount;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

}
