package main.cl.dagserver.infra.adapters.output.repositories;

import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;
import org.json.JSONObject;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
@ImportResource("classpath:properties-config.xml")
public class InternalStorage {
	
	
	private String locatedb = null;
	@SuppressWarnings("rawtypes")
	private ConcurrentMap map = null;

	
	public InternalStorage(@Value("${param.xcompath}")String xcomfolder) {
		deleteExistingFile(xcomfolder);
		DB db = DBMaker.fileDB(xcomfolder).fileDeleteAfterClose().make();
		map = db.hashMap("xcom").createOrOpen();
	}
	
	public void init(String name ) {
		this.locatedb = name;
	}
	
	public String getLocatedb() {
		return  locatedb;
	}
	@SuppressWarnings( "unchecked" )
	public void put(JSONObject json) {
		map.put(locatedb, json.toString());	
	}
	public JSONObject get() {
		String jsonstr = (String) map.get(locatedb);
		return new JSONObject(jsonstr);
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
}
