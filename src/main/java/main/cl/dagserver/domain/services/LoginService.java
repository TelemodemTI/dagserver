package main.cl.dagserver.domain.services;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import main.cl.dagserver.application.ports.input.LoginUseCase;
import main.cl.dagserver.application.ports.output.AuthenticationOutputPort;
import main.cl.dagserver.domain.core.BaseServiceComponent;
import main.cl.dagserver.domain.enums.AccountType;
import main.cl.dagserver.domain.model.SessionDTO;


@Service
public class LoginService  extends BaseServiceComponent implements LoginUseCase {

	@Value("${param.application.username}")
	private String username;
	@Value("${param.application.password}")
	private String password;
	@Value("${param.application.init.apikey}")
	private String apiKey;
	
	private final AuthenticationOutputPort auth;
	
	
    @Autowired
    public LoginService(AuthenticationOutputPort auth) {
        this.auth = auth;
    }
	
    @PostConstruct
    public void start() {
    	if(!username.trim().isEmpty() && password.trim().isEmpty() ) {
    		String sha256hex = org.apache.commons.codec.digest.DigestUtils.sha256Hex(password);  
    		this.repository.createAccount(username,AccountType.ADMIN.toString(),sha256hex);
    		
    		
    	}
    	if(!apiKey.trim().isEmpty()) {
    		this.repository.setProperty("default", "API KEY for default", apiKey, "HTTP_CHANNEL_API_KEY");	
    	}
    	
    	
    }
	
	
	@Override
	public SessionDTO apply(String t) {
		try {
			byte[] decodedBytes = Base64.getDecoder().decode(t);
            String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(decodedString);
            return this.auth.login(jsonObject);
		} catch (Exception e) {
			SessionDTO dto = new SessionDTO();
			dto.setRefreshToken("");
			dto.setToken("");
			return dto;
		}
	}


}