package main.cl.dagserver.infra.adapters.output.repositories;

import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import org.json.JSONObject;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;
import com.nhl.dflib.DataFrame;
import lombok.extern.log4j.Log4j2;
import main.cl.dagserver.domain.core.MetadataManager;

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
	public void put(Map<String,DataFrame> xcom) {
		JSONObject wrapper = new JSONObject();
		var keys = xcom.keySet();
		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String string = iterator.next();
			wrapper.put(string, MetadataManager.dataFrameToJson(xcom.get(string)));
		}
		map.put(locatedb, wrapper.toString());	
	}
	@SuppressWarnings("unchecked")
	public List<String> getKeys() {
		return new ArrayList<String>(map.keySet());
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
	public Map<String,DataFrame> get() {
		return this.getEntry(locatedb);
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
