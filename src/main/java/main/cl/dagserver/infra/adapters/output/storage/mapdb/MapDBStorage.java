package main.cl.dagserver.infra.adapters.output.storage.mapdb;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import org.json.JSONObject;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;
import com.nhl.dflib.DataFrame;
import lombok.extern.log4j.Log4j2;
import main.cl.dagserver.application.ports.output.Storage;
import main.cl.dagserver.domain.core.ExceptionEventLog;
import main.cl.dagserver.domain.core.MetadataManager;

@Component
@Log4j2
@ImportResource("classpath:properties-config.xml")
public class MapDBStorage implements Storage {

	private static final String EXCEPTIONS =  "exceptions";
	
	@SuppressWarnings("rawtypes")
	private ConcurrentMap map = null;
	
	@Value("${param.xcompath}")
	private String xcomfolder;
	
	@Value("${param.storage.exception}")
	private String exceptionstoragefile;
	
	public MapDBStorage(@Value("${param.xcompath}") String xcomfolder,@Value("${param.storage.exception}") String exceptionstoragefile) {	
		this.exceptionstoragefile = exceptionstoragefile;
		this.xcomfolder = xcomfolder;
		this.initInternalStorage();
	}
	private void initInternalStorage() {
		deleteExistingFile(xcomfolder);
		DB db = DBMaker.fileDB(xcomfolder).fileDeleteAfterClose().make();
		map = db.hashMap("xcom").createOrOpen();
	}
	@SuppressWarnings( "unchecked" )
	public void putEntry(String locatedb,Map<String,DataFrame> xcom) {
		JSONObject wrapper = new JSONObject();
		var keys = xcom.keySet();
		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String string = iterator.next();
			wrapper.put(string, MetadataManager.dataFrameToJson(xcom.get(string)));
		}
		map.put(locatedb, wrapper.toString());	
	}
	public Map<String,DataFrame> getEntry(String xcomkey) {
		Map<String,DataFrame> mapa = new HashMap<>();
		try {
			JSONObject wrapper = new JSONObject( (String) map.get(xcomkey));
			var keys = wrapper.keySet();
			for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
				String stepname = iterator.next();
				DataFrame df = MetadataManager.jsonToDataFrame(wrapper.getJSONArray(stepname));
				mapa.put(stepname, df);
			}	
		} catch (Exception e) {
			log.debug("no se ha encontrado key: {}",xcomkey);
		}
		return mapa;
	}
	private void deleteExistingFile(String xcomfolder) {
        try {
        	File file = new File(xcomfolder);
            if (file.exists()) {
                Files.delete(file.toPath());
            }	
		} catch (Exception e) {
			log.error(e);
		}
		
    }
	@SuppressWarnings("unchecked")
	public void deleteXCOM(Date time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		var keys = map.entrySet();
		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String key = iterator.next();
			try {
				Date dk = sdf.parse(key);
				if(dk.before(time)) {
					map.remove(key);
				}	
			} catch (Exception e) {
				log.debug("key {} not removed from xcom",key);
			}
		}
		
	}
	@SuppressWarnings("rawtypes")
	public void removeException(String eventDt) {
	    DB db = null;
	    HTreeMap map = null;
	    try {
	        db = DBMaker.fileDB(exceptionstoragefile).make();
	        map = db.hashMap(EXCEPTIONS).createOrOpen();
	        map.remove(eventDt);
	    } finally {
	        if (map != null) {
	            map.close();
	        }
	        if (db != null) {
	            db.close();
	        }
	    }
	}

	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addException(ExceptionEventLog event) {
		try(DB db = DBMaker.fileDB(exceptionstoragefile).make();
			HTreeMap map = db.hashMap(EXCEPTIONS).createOrOpen();){
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
			map.put(sdf.format(new Date()), excpd);	
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Map<String, Object> listException() {
		try(DB db = DBMaker.fileDB(exceptionstoragefile).make();
			HTreeMap map = db.hashMap(EXCEPTIONS).createOrOpen();){
			return new HashMap<>(map);
        } 
	}
}
