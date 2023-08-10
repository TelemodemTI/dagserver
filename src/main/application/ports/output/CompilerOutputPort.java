package main.application.ports.output;

import org.json.JSONArray;

import main.domain.exceptions.DomainException;

public interface CompilerOutputPort {

	void createJar(String bin, Boolean force) throws DomainException;
	JSONArray operators() throws DomainException;
	
}
