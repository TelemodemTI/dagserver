package main.cl.dagserver.application.ports.output;

import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.AuthDTO;


public interface CompilerOutputPort {

	void createJar(String bin, Boolean force,Properties props, AuthDTO objauth) throws DomainException;
	JSONArray operators() throws DomainException;
	void deleteJarfile(String jarname) throws DomainException;
	JSONObject reimport(String jarname) throws DomainException;
	
}
