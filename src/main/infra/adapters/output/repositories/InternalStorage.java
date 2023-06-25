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
		try (FileWriter file = new FileWriter(locatedb)) {
		    file.write(json.toString());
		    file.flush();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	public JSONObject get() throws Exception {
		StringBuilder content = new StringBuilder();
		FileReader reader = new FileReader(locatedb);
        int character;
        while ((character = reader.read()) != -1) {
                content.append((char) character);
        }
        JSONObject jsonObject = new JSONObject(content.toString());
        reader.close();
        return  jsonObject;
	}
	
}
