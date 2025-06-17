package main.cl.dagserver.unitest.domain.services;

import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import main.cl.dagserver.application.ports.output.StorageOutputPort;
import main.cl.dagserver.domain.core.ExceptionEventLog;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.services.InternalEventsService;

class InternalEventsServiceTest {

	@Mock
	StorageOutputPort storage;
	
	private InternalEventsService service;
	
	@BeforeEach
    void init() {
		storage = mock(StorageOutputPort.class);
		service = new InternalEventsService(storage);
	}
	
	@Test
	void registerExceptionTest() {
		service.registerException(new ExceptionEventLog(new Object(), new DomainException(new Exception("test")),"mensaje"));
		assertTrue(true);
	}
}