package main.cl.dagserver.infra.adapters.input.events;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import main.cl.dagserver.application.ports.input.ExceptionListenerUseCase;

class ExceptionEventListenerTest {
	@Mock()
	ExceptionListenerUseCase handler;
	private ExceptionEventListener listener;
	
	@BeforeEach
    void init() {
		handler = mock(ExceptionListenerUseCase.class);
		listener = new ExceptionEventListener(handler); 
	}
	
	@Test
	void onApplicationEventTest() {
		listener.onApplicationEvent(null);
		assertTrue(true);
	}
}