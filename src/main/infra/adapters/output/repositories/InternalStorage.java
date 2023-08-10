package main.infra.adapters.output.repositories;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.json.JSONObject;


public class InternalStorage {

	private String locatedb = null;

	public InternalStorage(String path ) {
		this.locatedb = path;
	}
	
	public String getLocatedb() {
		return  locatedb;
	}
	public void put(JSONObject json) {
		if(json.length() > 0) {
			try (FileWriter file = new FileWriter(locatedb)) {
			    file.write(json.toString());
			    file.flush();
			} catch (IOException e) {
			    e.printStackTrace();
			}	
		}
	}
	public JSONObject get() throws Exception {
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
