package main.cl.dagserver.domain.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class CredentialsDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private String username;
	private String password;
}
