package main.cl.dagserver;

import java.util.Map;
import java.util.Map.Entry;
import org.junit.jupiter.api.Test;
import org.mapdb.DB;
import org.mapdb.DBMaker;

public class ExceptionStorageReader {
	
	@Test
	void readAllSavedExceptions(){
		this.start();
	}
	
	public void start() {
	        DB db = null;
	        try {
	        	db = DBMaker.fileDB("C:\\tmp\\dagrags\\exception.db").make();
				for (final Entry<String, Object> entry : db.getAll().entrySet()) {
	                final String name = entry.getKey();
	                final Object value = entry.getValue();
	                if (value instanceof Map) {
	                    inspectMap(name, (Map<?, ?>) value);
	                } else {
	                    System.err.println(String.format("Unexpected type (%s) for '%s'.", value.getClass(), name));
	                }
	            }
	        } finally {
	            if (db != null) {
	                db.close();
	            }
	        }
	    }

	    private static <K, V> void inspectMap(final String name, final Map<K, V> map) {
	        System.out.println(name);
	        for (final Entry<K, V> entry : map.entrySet()) {
	            final K key = entry.getKey();
	            final V value = entry.getValue();
	            System.out.println(String.format("    %s = %s [%s, %s]", key, value, key.getClass(), value.getClass()));
	        }
	    }

	
}
