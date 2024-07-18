package main.cl.dagserver.domain.services;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import main.cl.dagserver.application.ports.input.LoginUseCase;
import main.cl.dagserver.application.ports.output.AuthenticationOutputPort;
import main.cl.dagserver.domain.model.SessionDTO;


@Service
public class LoginService implements LoginUseCase {

	private final AuthenticationOutputPort auth;

    @Autowired
    public LoginService(AuthenticationOutputPort auth) {
        this.auth = auth;
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