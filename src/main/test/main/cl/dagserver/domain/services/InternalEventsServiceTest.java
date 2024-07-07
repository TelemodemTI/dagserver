package main.cl.dagserver.domain.services;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import main.cl.dagserver.application.ports.output.Storage;
import main.cl.dagserver.domain.core.ExceptionEventLog;
import main.cl.dagserver.domain.exceptions.DomainException;

class InternalEventsServiceTest {

	@Mock
	Storage storage;
	
	private InternalEventsService service;
	
	@BeforeEach
    void init() {
		storage = mock(Storage.class);
		service = new InternalEventsService(storage);
	}
	
	@Test
	void registerExceptionTest() {
		service.registerException(new ExceptionEventLog(new Object(), new DomainException(new Exception("test")),"mensaje"));
		assertTrue(true);
	}
}
