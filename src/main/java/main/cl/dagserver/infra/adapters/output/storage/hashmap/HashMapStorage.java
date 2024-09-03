package main.cl.dagserver.infra.adapters.output.storage.hashmap;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONObject;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import com.nhl.dflib.DataFrame;
import lombok.extern.log4j.Log4j2;
import main.cl.dagserver.application.ports.output.StorageOutputPort;
import main.cl.dagserver.domain.core.DataFrameUtils;
import main.cl.dagserver.domain.core.ExceptionEventLog;

@Component
@Log4j2
@ImportResource("classpath:properties-config.xml")
@Profile("storage-hashmap")
public class HashMapStorage implements StorageOutputPort {

	
	private Map<String,Object> map = new HashMap<>();
	private Map<String,Object> mapExceptions = new HashMap<>();
	
	@Override
	public void putEntry(String locatedb, Map<String, DataFrame> xcom) {
		JSONObject wrapper = new JSONObject();
		var keys = xcom.keySet();
		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String string = iterator.next();
			wrapper.put(string, DataFrameUtils.dataFrameToJson(xcom.get(string)));
		}
		map.put(locatedb, wrapper.toString());
	}

	@Override
	public Map<String, DataFrame> getEntry(String xcomkey) {
		Map<String,DataFrame> mapa = new HashMap<>();
		try {
			if(map.containsKey(xcomkey)) {
				JSONObject wrapper = new JSONObject( (String) map.get(xcomkey));
				var keys = wrapper.keys();
				for (Iterator<String> iterator = keys; iterator.hasNext();) {
					String stepname = iterator.next();
					DataFrame df = DataFrameUtils.jsonToDataFrame(wrapper.getJSONArray(stepname));
					mapa.put(stepname, df);
				}	
			}
		} catch (Exception e) {
			log.debug("no se ha encontrado key: {}",xcomkey);
		}
		return mapa;
	}

	@Override
	public void deleteXCOM(Date time) {
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
	    Iterator<String> iterator = map.keySet().iterator();
	    while (iterator.hasNext()) {
	        String key = iterator.next();
	        try {
	            Date dk = sdf.parse(key);
	            if (dk.before(time)) {
	                iterator.remove();
	            }
	        } catch (Exception e) {
	            log.debug("key {} not removed from xcom", key);
	        }
	    }
	}

	@Override
	public void removeException(String eventDt) {
		mapExceptions.remove(eventDt);
	}

	@Override
	public void addException(ExceptionEventLog event) {
		String classname = event.getSource().getClass().getCanonicalName();
		String method = event.getMessage();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMsshhmmss");
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		event.getException().printStackTrace(printWriter);
		String stacktrace = stringWriter.toString();      
		Map<String,String> excpd = new HashMap<>();
		excpd.put("classname", classname);
		excpd.put("method",method);
		excpd.put("stacktrace",stacktrace);
		mapExceptions.put(sdf.format(new Date()), excpd);	
	}

	@Override
	public Map<String, Object> listException() {
		return new HashMap<>(mapExceptions);
	}

}
