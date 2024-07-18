package main.cl.dagserver.application.ports.output;

import org.json.JSONObject;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.AuthDTO;
import main.cl.dagserver.domain.model.SessionDTO;

public interface AuthenticationOutputPort {
	public SessionDTO login(JSONObject reqobject) throws DomainException;
	public AuthDTO untokenize(String token) throws DomainException;
	public void logout(String token) throws DomainException;	
}
