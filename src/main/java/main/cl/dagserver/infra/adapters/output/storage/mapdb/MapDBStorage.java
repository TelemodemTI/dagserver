package main.cl.dagserver.infra.adapters.output.storage.mapdb;

import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import lombok.extern.log4j.Log4j2;
import main.cl.dagserver.application.ports.output.StorageOutputPort;
import main.cl.dagserver.infra.adapters.output.storage.hashmap.HashMapStorage;

@Component
@Log4j2
@ImportResource("classpath:properties-config.xml")
@Profile("storage-map-db")
public class MapDBStorage extends HashMapStorage implements StorageOutputPort {

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
	@Override
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Map<String, Object> listException() {
		try(DB db = DBMaker.fileDB(exceptionstoragefile).make();
			HTreeMap map1 = db.hashMap(EXCEPTIONS).createOrOpen();){
			return new HashMap<>(map1);
        } 
	}
}
