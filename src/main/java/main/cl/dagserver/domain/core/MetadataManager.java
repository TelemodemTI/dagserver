package main.cl.dagserver.domain.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.nhl.dflib.DataFrame;

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
			par.put("opt", options);
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
	public static JSONArray dataFrameToJson(DataFrame df) {
        JSONArray jsonArray = new JSONArray();
        df.iterator().forEachRemaining(row -> {
        	JSONObject jsonObject = new JSONObject();
            for (String columnName : df.getColumnsIndex()) {
                jsonObject.put(columnName, row.get(columnName));
            }
            jsonArray.put(jsonObject);
        });
        return jsonArray;
	}
	public static DataFrame jsonToDataFrame(JSONArray arr) {
		JSONObject obj = arr.getJSONObject(0);
		var keys = new ArrayList<String>(obj.keySet());
		var appender = DataFrame.byArrayRow(keys.toArray(new String[0])).appender();
		for (int i = 0; i < arr.length(); i++) {
			JSONObject item = arr.getJSONObject(i);
			List<Object> values = new ArrayList<>();
			for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
				String key = iterator.next();
				var obji = item.get(key);
				values.add(obji);
			}
			appender.append(values.toArray(new Object[0]));
		}
		return appender.toDataFrame();
	}

	public static List<Map<String, Object>> dataFrameToList(DataFrame df) {
        List<Map<String, Object>> list = new ArrayList<>();
        df.iterator().forEachRemaining(row -> {
            Map<String, Object> map = new HashMap<>();
            for (String columnName : df.getColumnsIndex()) {
                map.put(columnName, row.get(columnName));
            }
            list.add(map);
        });
        return list;
    }
	
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
