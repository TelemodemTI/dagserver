package main.cl.dagserver.infra.adapters.output.storage;

import static org.testng.Assert.assertTrue;
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
}
