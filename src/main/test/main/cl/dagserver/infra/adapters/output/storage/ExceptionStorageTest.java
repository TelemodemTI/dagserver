package main.cl.dagserver.infra.adapters.output.storage;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import main.cl.dagserver.domain.core.ExceptionEventLog;
import main.cl.dagserver.domain.exceptions.DomainException;

class ExceptionStorageTest {

	private ExceptionStorage storage = new ExceptionStorage(); 
	
	@BeforeEach
    public void init() {
		ReflectionTestUtils.setField(storage, "exceptionstoragefile", "c:\\tmp\\dagrags\\testdag");
	}
	
	
	
	@Test
	void addTest() {
		ExceptionEventLog event = new ExceptionEventLog(new Object(),new DomainException(new Exception("test")),"message"); 
		storage.add(event);
		assertTrue(true);
	}
	
	 @Test
	 void removeTest() {
	        // First add an exception to the storage
	        ExceptionEventLog event = new ExceptionEventLog(new Object(), new DomainException(new Exception("test to remove")), "message");
	        storage.add(event);

	        // Get the event date key to remove
	        Map<String, Object> storedExceptions = storage.list();
	        String eventDt = storedExceptions.keySet().iterator().next(); // Get the first key

	        // Now remove the exception
	        storage.remove(eventDt);

	        // Verify that the exception is removed
	        storedExceptions = storage.list();
	        assertFalse(storedExceptions.containsKey(eventDt));
	 }
}
