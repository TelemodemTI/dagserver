package main.cl.dagserver.application.pojos;

import lombok.Data;

@Data
public class SessionPOJO {
	private String token;
	private String refreshToken;
	
}
