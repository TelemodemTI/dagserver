package main.cl.dagserver.domain.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

public class Dagmap extends HashMap<String, Object> {

	
	private static final long serialVersionUID = 1L;
	private Map<String, Class<?>> typeMap = new HashMap<>();
    private Map<String, Long> creationTimeMap = new HashMap<>();
    private Map<String, Long> lastModifiedTimeMap = new HashMap<>();

    @Override
    public Object put(String key, Object value) {
        Object oldValue = super.put(key, value);
        long currentTime = System.currentTimeMillis();
        if (!creationTimeMap.containsKey(key)) {
            creationTimeMap.put(key, currentTime);
        }
        lastModifiedTimeMap.put(key, currentTime);
        if (value != null && value.getClass().isPrimitive()) {
            typeMap.put(key, value.getClass());
        }
        return oldValue;
    }

    @Override
    public Object remove(Object key) {
        Object removedValue = super.remove(key);
        if (removedValue != null) {
            lastModifiedTimeMap.remove(key);
            creationTimeMap.remove(key);
            typeMap.remove(key);
        }
        return removedValue;
    }


    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        long currentTime = System.currentTimeMillis();
        for (Map.Entry<String, Object> entry : entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            jsonObject.put(key, value);
            if (!creationTimeMap.containsKey(key)) {
                creationTimeMap.put(key, currentTime);
            }
            lastModifiedTimeMap.put(key, currentTime);
            if (value != null && value.getClass().isPrimitive()) {
                typeMap.put(key, value.getClass());
            }
        }
        return jsonObject;
    }
    
    public static Dagmap fromJSONObject(JSONObject jsonObject) {
    	Dagmap jsonMap = new Dagmap();
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            jsonMap.put(key, value);
        }
        return jsonMap;
    }

    public static List<Dagmap> createDagmaps(int numberOfMaps, String defaultKey, Object defaultValue) {
        List<Dagmap> dagmaps = new ArrayList<>();
        for (int i = 0; i < numberOfMaps; i++) {
            Dagmap dagmap = new Dagmap();
            dagmap.put(defaultKey, defaultValue);
            dagmaps.add(dagmap);
        }
        return dagmaps;
    }
    
    public static List<Dagmap> convertToDagmaps(List<Map<String, Object>> maps) {
        List<Dagmap> dagmaps = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            Dagmap dagmap = new Dagmap();
            dagmap.putAll(map);
            dagmaps.add(dagmap);
        }
        return dagmaps;
    }
}
