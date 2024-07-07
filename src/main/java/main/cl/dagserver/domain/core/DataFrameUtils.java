package main.cl.dagserver.domain.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import com.nhl.dflib.DataFrame;


public class DataFrameUtils {

	public static byte[] dataFrameToBytes(DataFrame df) {
		JSONArray var1 = DataFrameUtils.dataFrameToJson(df);
		return var1.toString().getBytes();
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
	public static DataFrame createStatusFrame(String status) {
		return DataFrame
		        .byArrayRow("status") 
		        .appender() 
		        .append(status)   
		        .toDataFrame();
    }
	public static DataFrame createFrame(String key,Object value) {
		return DataFrame
		        .byArrayRow(key) 
		        .appender() 
		        .append(value)   
		        .toDataFrame();
	}

	@SuppressWarnings("removal")
	public static DataFrame buildDataFrameFromMap(List<Map<String, Object>> list) {
        if (list == null || list.isEmpty()) {
            return DataFrame.newFrame("status").empty();
        } else {
        	Map<String, Object> firstRow = list.get(0);
            String[] columns = firstRow.keySet().toArray(new String[0]);
            var apender = DataFrame.byArrayRow(columns).appender();
            for (Iterator<Map<String, Object>> iterator = list.iterator(); iterator.hasNext();) {
    			Map<String, Object> map = iterator.next();
    			Object[] valuesArray = map.values().toArray(new Object[0]);
    			apender.append(valuesArray);
    		}
            return apender.toDataFrame();	
        }
         
    }
	@SuppressWarnings({ "removal", "unchecked" })
	public static DataFrame buildDataFrameFromObject(List<Object> list) {
        if (list == null || list.isEmpty()) {
            return DataFrame.newFrame("status").empty();
        } else {
        	Object raw = list.get(0);
        	if(raw instanceof Map) {
        		var newl = list.stream().map(item -> (Map<String, Object>) item).collect(Collectors.toList());
        		return DataFrameUtils.buildDataFrameFromMap(newl);
        	} else {
        		Map<String, Object> firstRow = new HashMap<>();
            	firstRow.put("content", raw);
            	String[] columns = new String[1];
            	columns[0] = "content";
            	var apender = DataFrame.byArrayRow(columns).appender();		
            	for (Iterator<Object> iterator = list.iterator(); iterator.hasNext();) {
            			Object data =  iterator.next();
            			Map<String, Object> map = new HashMap<>();
            			map.put("content", data);
            			Object[] valuesArray = map.values().toArray(new Object[0]);
            			apender.append(valuesArray);
            	}
            	return apender.toDataFrame();
        	}
        }
         
    }

}
