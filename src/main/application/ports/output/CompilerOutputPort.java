package main.application.ports.output;

import org.json.JSONArray;

public interface CompilerOutputPort {

	void createJar(String bin, Boolean force) throws Exception;
	JSONArray operators() throws Exception;
	
}
