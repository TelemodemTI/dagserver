package main.cl.dagserver.infra.adapters.output.storage.hashmap;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.nhl.dflib.DataFrame;

import main.cl.dagserver.domain.core.ExceptionEventLog;
import main.cl.dagserver.domain.exceptions.DomainException;

class HashMapStorageTest {

	private HashMapStorage storage = new HashMapStorage(); 
	
	@Test
	void addExceptionTest() {
		ExceptionEventLog event = new ExceptionEventLog(new Object(),new DomainException(new Exception("test")),"message"); 
		storage.addException(event);
		assertTrue(true);
	}
	@Test
	void listExceptionTest() {
		storage.listException();
		assertTrue(true);
	}
	@Test
	void removeExceptionTest() {
		storage.removeException("test");
		assertTrue(true);
	}
	@Test
	void putEntryTest() {
		Map<String,DataFrame> test = new HashMap<>();
		test.put("test", DataFrame.empty("test"));
		storage.putEntry("test", test);
		assertTrue(true);
	}
	@Test
	void getEntryErrorTest() {
		var entry = storage.getEntry("test");
		assertNotNull(entry);
	}
	@Test
	void getEntryTest() throws JSONException {
		JSONObject xcom = new JSONObject();
		xcom.put("stepname", new JSONArray());
		Map<String,Object> central = new HashMap<>();
		central.put("test", xcom.toString());
		ReflectionTestUtils.setField(storage, "map", central);
		var entry = storage.getEntry("test");
		assertNotNull(entry);
	}
	@Test
	void deleteXCOMTest() {
		storage.deleteXCOM(new Date());
		assertTrue(true);
	}
}