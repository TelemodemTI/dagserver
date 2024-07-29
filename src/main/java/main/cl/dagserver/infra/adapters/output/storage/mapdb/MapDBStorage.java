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
import org.json.JSONArray;
import org.json.JSONObject;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.springframework.beans.factory.annotation.Value;
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
@Profile("storage-map-db")
public class MapDBStorage implements StorageOutputPort {

	private static final String EXCEPTIONS =  "exceptions";
	private static final String XCOM =  "xcom";
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
		var map = db.hashMap(XCOM).createOrOpen();
		map.close();
		db.close();
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
	@SuppressWarnings("rawtypes")
	@Override
	public void removeException(String eventDt) {
	    DB db = null;
	    HTreeMap map1 = null;
	    try {
	        db = DBMaker.fileDB(exceptionstoragefile).make();
	        map1 = db.hashMap(EXCEPTIONS).createOrOpen();
	        map1.remove(eventDt);
	    } finally {
	        if (map1 != null) {
	            map1.close();
	        }
	        if (db != null) {
	            db.close();
	        }
	    }
	}
	@SuppressWarnings("unchecked")
	@Override
	public void addException(ExceptionEventLog evento) {
		DB db = null;
	    HTreeMap<String,Object> map1 = null;
	    try {
	        db = DBMaker.fileDB(exceptionstoragefile).make();
	        map1 = (HTreeMap<String, Object>) db.hashMap(EXCEPTIONS).createOrOpen();
	        String classname = evento.getSource().getClass().getCanonicalName();
			String method = evento.getMessage();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMsshhmmss");
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			evento.getException().printStackTrace(printWriter);
			String stacktrace = stringWriter.toString();      
			Map<String,String> excpd = new HashMap<>();
			excpd.put("classname", classname);
			excpd.put("method",method);
			excpd.put("stacktrace",stacktrace);
			map1.put(sdf.format(new Date()), excpd);
	    } finally {
	        if (map1 != null) {
	            map1.close();
	        }
	        if (db != null) {
	            db.close();
	        }
	    }
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Map<String, Object> listException() {
		try(DB db = DBMaker.fileDB(exceptionstoragefile).make();
			HTreeMap map1 = db.hashMap(EXCEPTIONS).createOrOpen();){
			return new HashMap<>(map1);
        } 
	}
	
	
	@SuppressWarnings("unchecked")
	public void putEntry(String locatedb,Map<String,DataFrame> xcom) {
		DB db = null;
	    HTreeMap<String,Object> map1 = null;
	    try {
	        db = DBMaker.fileDB(xcomfolder).make();
	        map1 = (HTreeMap<String, Object>) db.hashMap(XCOM).createOrOpen();
	        JSONObject wrapper = new JSONObject();
			var keys = xcom.keySet();
			for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
				String string = iterator.next();
				var df = xcom.get(string);
				JSONArray jsonarray = DataFrameUtils.dataFrameToJson(df);
				wrapper.put(string, jsonarray);
			}
			map1.put(locatedb, wrapper.toString());
	    } finally {
	        if (map1 != null) {
	            map1.close();
	        }
	        if (db != null) {
	            db.close();
	        }
	    }
	}
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, DataFrame> getEntry(String xcomkey) {
		DB db = null;
	    HTreeMap<String,Object> map1 = null;
	    Map<String,DataFrame> mapa = new HashMap<>();
	    try {
	        db = DBMaker.fileDB(xcomfolder).make();
	        map1 = (HTreeMap<String, Object>) db.hashMap(XCOM).createOrOpen();
	        
			try {
				JSONObject wrapper = new JSONObject( (String) map1.get(xcomkey));
				var keys = wrapper.keys();
				for (Iterator<String> iterator = keys; iterator.hasNext();) {
					String stepname = iterator.next();
					DataFrame df = DataFrameUtils.jsonToDataFrame(wrapper.getJSONArray(stepname));
					mapa.put(stepname, df);
				}	
			} catch (Exception e) {
				log.debug("no se ha encontrado key: {}",xcomkey);
			}
	    } finally {
	        if (map1 != null) {
	            map1.close();
	        }
	        if (db != null) {
	            db.close();
	        }
	    }
	    return mapa;	
	}
	@SuppressWarnings("unchecked")
	@Override
	public void deleteXCOM(Date time) {
		DB db = null;
	    HTreeMap<String,Object> map1 = null;
	    try {
	        db = DBMaker.fileDB(xcomfolder).make();
	        map1 = (HTreeMap<String, Object>) db.hashMap(XCOM).createOrOpen();
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		    Iterator<String> iterator = map1.keySet().iterator();
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
	    } finally {
	        if (map1 != null) {
	            map1.close();
	        }
	        if (db != null) {
	            db.close();
	        }
	    }
	}
	
}
