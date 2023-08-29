package main.domain.services;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import main.application.ports.output.JarSchedulerOutputPort;
import main.application.ports.output.SchedulerRepositoryOutputPort;
import main.domain.exceptions.DomainException;
import main.domain.model.EventListenerDTO;

class SchedulerQueryHandlerServiceTest {
	
	private SchedulerQueryHandlerService service = new SchedulerQueryHandlerService();
	
	@Mock
	private JarSchedulerOutputPort scanner;
	
	@Mock
	private SchedulerRepositoryOutputPort repository;
	
	@BeforeEach
    public void init() {
		scanner = mock(JarSchedulerOutputPort.class);
		repository = mock(SchedulerRepositoryOutputPort.class);
		ReflectionTestUtils.setField(service, "scanner", scanner);
		ReflectionTestUtils.setField(service, "repository", repository);
	}
	
	@Test
	void listScheduledJobsTest() throws DomainException {
		Map<String,Object> item = new HashMap<>();
		List<Map<String,Object>> realscheduled = new ArrayList<>();
		realscheduled.add(item);
		
		List<EventListenerDTO> list = new ArrayList<>();
		EventListenerDTO pop = new EventListenerDTO();
		pop.setOnStart("test");
		pop.setOnEnd("test");
		list.add(pop);
		
		when(scanner.listScheduled()).thenReturn(realscheduled);
		when(repository.listEventListeners()).thenReturn(list);
		var item2 = service.listScheduledJobs();
		assertNotNull(item2);
	}
}
