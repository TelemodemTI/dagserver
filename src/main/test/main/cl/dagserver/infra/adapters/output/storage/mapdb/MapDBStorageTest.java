package main.cl.dagserver.infra.adapters.output.storage.mapdb;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.nhl.dflib.DataFrame;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import main.cl.dagserver.domain.core.ExceptionEventLog;
import main.cl.dagserver.domain.exceptions.DomainException;

public class MapDBStorageTest {
	private MapDBStorage storage = new MapDBStorage("c:\\tmp\\dagrags\\xcom.db","c:\\tmp\\dagrags\\exceptions.db");
	
	@Test
	void removeExceptionTest() {
		storage.removeException("20240101");
		assertTrue(true);
	}
	@Test
	void addExceptionTest() {
		ExceptionEventLog log = new ExceptionEventLog("test",new DomainException(new Exception("test")),"test");
		storage.addException(log);
		assertTrue(true);
	}
	@Test
	void listExceptionTest() {
		var returned = storage.listException();
		assertNotNull(returned);
	}
	@Test
	void putEntry() {
		Map<String,DataFrame> xcom = new HashMap<>();
		DataFrame df =  DataFrame.byArrayRow("status").appender().append("test").toDataFrame();
		xcom.put("test", df);
		storage.putEntry("20240101", xcom);
		assertTrue(true);
	}
	@Test
	void getEntryTest() {
		Map<String,DataFrame> xcom = new HashMap<>();
		DataFrame df =  DataFrame.byArrayRow("status").appender().append("test").toDataFrame();
		xcom.put("test", df);
		storage.putEntry("20240101", xcom);
		var entry = storage.getEntry("20240101");
		assertNotNull(entry);
	}
	@Test
	void deleteXCOMTest() {
		Map<String,DataFrame> xcom = new HashMap<>();
		DataFrame df =  DataFrame.byArrayRow("status").appender().append("test").toDataFrame();
		xcom.put("test", df);
		var dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		storage.putEntry(sdf.format(dt), xcom);
		storage.deleteXCOM(dt);
		assertTrue(true);
	}
}
