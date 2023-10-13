package main.application.ports.output;

import java.util.Properties;

import org.json.JSONArray;

import main.domain.exceptions.DomainException;

public interface CompilerOutputPort {

	void createJar(String bin, Boolean force,Properties props) throws DomainException;
	JSONArray operators() throws DomainException;
	void deleteJarfile(String jarname) throws DomainException;
	
}
