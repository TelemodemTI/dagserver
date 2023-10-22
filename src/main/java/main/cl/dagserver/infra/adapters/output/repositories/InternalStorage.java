package main.cl.dagserver.infra.adapters.output.repositories;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.json.JSONObject;

import main.cl.dagserver.domain.exceptions.DomainException;

public class InternalStorage {
	
	private String locatedb = null;

	public InternalStorage(String path ) {
		this.locatedb = path;
	}
	
	public String getLocatedb() {
		return  locatedb;
	}
	public void put(JSONObject json) throws DomainException {
		if(json.length() > 0) {
			try (FileWriter file = new FileWriter(locatedb)) {
			    file.write(json.toString());
			    file.flush();
			} catch (IOException e) {
			    throw new DomainException(e);
			}	
		}
	}
	public JSONObject get() {
		StringBuilder content = new StringBuilder();
		try(FileReader reader = new FileReader(locatedb);) {
	        int character;
	        while ((character = reader.read()) != -1) {
	                content.append((char) character);
	        }
	        return new JSONObject(content.toString());
		} catch (Exception e) {
			return new JSONObject();
		}
	}
	
}
