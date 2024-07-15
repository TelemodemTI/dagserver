package main.cl.dagserver.domain.model;

import java.util.Date;

import lombok.Data;
import main.cl.dagserver.domain.enums.AccountType;

@Data
public class AuthDTO {
	private Date expires;
	private Date issueAt;
	private String subject;
	private AccountType accountType;
}
