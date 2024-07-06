package main.cl.dagserver.infra.adapters.output.storage;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import main.cl.dagserver.domain.core.ExceptionEventLog;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.infra.adapters.output.storage.mapdb.MapDBStorage;

class ExceptionStorageTest {

	private MapDBStorage storage = new MapDBStorage("",""); 
	
	@BeforeEach
    public void init() {
		ReflectionTestUtils.setField(storage, "exceptionstoragefile", "c:\\tmp\\dagrags\\testdag");
	}

	@Test
	void addTest() {
		ExceptionEventLog event = new ExceptionEventLog(new Object(),new DomainException(new Exception("test")),"message"); 
		storage.addException(event);
		assertTrue(true);
	}
	
	 @Test
	 void removeTest() {
	        // First add an exception to the storage
	        ExceptionEventLog event = new ExceptionEventLog(new Object(), new DomainException(new Exception("test to remove")), "message");
	        storage.addException(event);

	        // Get the event date key to remove
	        Map<String, Object> storedExceptions = storage.listException();
	        String eventDt = storedExceptions.keySet().iterator().next(); // Get the first key

	        // Now remove the exception
	        storage.removeException(eventDt);

	        // Verify that the exception is removed
	        storedExceptions = storage.listException();
	        assertFalse(storedExceptions.containsKey(eventDt));
	 }
}
