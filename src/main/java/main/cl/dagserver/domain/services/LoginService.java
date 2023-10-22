package main.cl.dagserver.domain.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Service;
import main.cl.dagserver.domain.core.TokenEngine;
import main.cl.dagserver.domain.model.UserDTO;

import main.cl.dagserver.application.ports.input.LoginUseCase;
import main.cl.dagserver.application.ports.output.SchedulerRepositoryOutputPort;


@Service
@ImportResource("classpath:properties-config.xml")
public class LoginService implements LoginUseCase ,Function<List<String>,String> {

	@Autowired
	private TokenEngine tokenEngine;
	
	@Autowired
	private SchedulerRepositoryOutputPort repository;
	
	@Value( "${param.jwt_secret}" )
	private String jwtSecret;
	
	@Value( "${param.jwt_signer}" )
	private String jwtSigner;
	
	@Value( "${param.jwt_subject}" )
	private String jwtSubject;
	
	@Value( "${param.jwt_ttl}" )
	private Integer jwtTtl;
	
	
	
	private String login(String username,String hash) {
		List<UserDTO> list = repository.findUser(username);
		if(!list.isEmpty() ) {
			UserDTO user = list.get(0);
			if(hash.equals(user.getPwdhash())) {
				Map<String,String> claims = new HashMap<>();
				claims.put("typeAccount", user.getTypeAccount());
				claims.put("username", username);
				claims.put("userid", user.getId().toString());
				return tokenEngine.tokenize(jwtSecret, jwtSigner, jwtSubject, jwtTtl, claims);
			} else {
				return "";
			}
		} else {
			return "";
		}
	}	
	@Override
	public String apply(List<String> t) {
		var username = t.get(0);
		var pwd = t.get(1);
		try {
			return this.login(username, pwd);
		} catch (Exception e) {
			return "";
		}
	}

}