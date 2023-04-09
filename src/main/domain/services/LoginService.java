package main.domain.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Service;
import main.domain.core.TokenEngine;
import main.application.ports.input.LoginUseCase;
import main.domain.entities.User;
import main.domain.repositories.SchedulerRepository;

@Service
@ImportResource("classpath:properties-config.xml")
public class LoginService implements LoginUseCase ,Function<List<String>,String> {

	@Autowired
	private SchedulerRepository repository;
	
	@Value( "${param.jwt_secret}" )
	private String jwt_secret;
	
	@Value( "${param.jwt_signer}" )
	private String jwt_signer;
	
	@Value( "${param.jwt_subject}" )
	private String jwt_subject;
	
	@Value( "${param.jwt_ttl}" )
	private Integer jwt_ttl;
	
	
	
	private String login(String username,String pwdhash) throws Exception {
		List<User> list = repository.findUser(username);
		if(list.size() > 0) {
			User user = list.get(0);
			String hash = TokenEngine.sha256(pwdhash);
			if(hash.equals(user.getPwdhash())) {
				Map<String,String> claims = new HashMap<String,String>();
				claims.put("username", username);
				claims.put("userid", user.getId().toString());
				String token = TokenEngine.tokenize(jwt_secret, jwt_signer, jwt_subject, jwt_ttl, claims);
				return token;
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
			e.printStackTrace();
			return "";
		}
	}

}