package main.cl.dagserver.infra.adapters.output.repositories;

import java.io.File;
import java.util.concurrent.ConcurrentMap;
import org.json.JSONObject;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

import groovyjarjarantlr4.v4.codegen.model.dbg;
import main.cl.dagserver.domain.exceptions.DomainException;

@Component
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void put(JSONObject json) throws DomainException {
		map.put(locatedb, json.toString());	
	}
	@SuppressWarnings({ "rawtypes" })
	public JSONObject get() {
		String jsonstr = (String) map.get(locatedb);
		return new JSONObject(jsonstr);
	}
	private void deleteExistingFile(String xcomfolder) {
        File file = new File(xcomfolder);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                System.out.println("Archivo existente eliminado con éxito.");
            } else {
                System.err.println("No se pudo eliminar el archivo existente.");
            }
        }
    }
}
