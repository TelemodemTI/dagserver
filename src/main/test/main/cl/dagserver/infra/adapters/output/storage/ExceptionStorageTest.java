package main.cl.dagserver.infra.adapters.output.storage;

import static org.testng.Assert.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.cl.dagserver.domain.core.ExceptionEventLog;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.infra.adapters.output.storage.mapdb.MapDBStorage;

class ExceptionStorageTest {

	private MapDBStorage storage = new MapDBStorage("c:\\tmp\\dagrags\\testdag","C:\\tmp\\dagrags\\exceptions.db"); 
	
	@BeforeEach
    public void init() {
		
		
	}

	@Test
	void addTest() {
		ExceptionEventLog event = new ExceptionEventLog(new Object(),new DomainException(new Exception("test")),"message"); 
		storage.addException(event);
		assertTrue(true);
	}
	
}
