package main.cl.dagserver.domain.services;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;

import main.cl.dagserver.application.ports.input.RedisChannelUseCase;
import main.cl.dagserver.application.ports.output.ExceptionStorageUseCase;
import main.cl.dagserver.domain.core.ExceptionEventLog;
import main.cl.dagserver.domain.exceptions.DomainException;

class InternalEventsServiceTest {

	@Mock
	ExceptionStorageUseCase storage;
	
	private InternalEventsService service;
	
	@BeforeEach
    void init() {
		storage = mock(ExceptionStorageUseCase.class);
		service = new InternalEventsService(storage);
	}
	
	@Test
	void registerExceptionTest() {
		service.registerException(new ExceptionEventLog(new Object(), new DomainException(new Exception("test")),"mensaje"));
		assertTrue(true);
	}
}
