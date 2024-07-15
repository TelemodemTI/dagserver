package main.cl.dagserver.application.ports.output;

import org.json.JSONObject;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.AuthDTO;

public interface AuthenticationOutputPort {
	public String login(JSONObject reqobject) throws DomainException;
	public AuthDTO untokenize(String token);	
}
