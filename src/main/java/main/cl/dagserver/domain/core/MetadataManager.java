package main.cl.dagserver.domain.core;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
public class MetadataManager {
	private String canonicalName;
	private JSONArray params;
	private JSONArray opts;
	private String type;
	
	public MetadataManager(String canonicalName) {
		this.canonicalName = canonicalName;
		this.params = new JSONArray();
		this.opts = new JSONArray();
	}
	
	public void setParameter(String key, String type) {
		this.setParameter(key, type, null);
	}
	
	public void setParameter(String key, String type, List<String> options) {
		var par = new JSONObject();
		par.put("name", key);
		par.put("type", type);
		if(options != null) {
			par.put("opt", (Object) options);
		}
		params.put(par);
	}
	
	public void setOpts(String key, String type) {
		this.setOpts(key, type, null);
	}
	
	public void setOpts(String key, String type, List<String> options) {
		var par = new JSONObject();
		par.put("name", key);
		par.put("type", type);
		if(options != null) {
			par.put("opt", new JSONArray(options));
		}
		opts.put(par);
	}
	
	public JSONObject generate() {
		String[] namearr = this.canonicalName.split("\\.");
		JSONObject tag = new JSONObject();
		tag.put("class", this.canonicalName);
		tag.put("name", namearr[namearr.length - 1]);
		tag.put("params", params);
		tag.put("opt", opts);
		tag.put("type", this.type);
		return tag;
	}
		
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
