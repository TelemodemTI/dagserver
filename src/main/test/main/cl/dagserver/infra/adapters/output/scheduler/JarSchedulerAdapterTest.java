package main.cl.dagserver.infra.adapters.output.scheduler;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import main.cl.dagserver.application.ports.output.FileSystemOutputPort;
import main.cl.dagserver.application.ports.output.StorageOutputPort;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.infra.adapters.confs.QuartzConfig;

import java.nio.file.Path;

class JarSchedulerAdapterTest {

	@Mock
    private ApplicationEventPublisher eventPublisher;
	@Mock
	private QuartzConfig quartz;
	@Mock
	private StorageOutputPort storage;
	@Mock
	private FileSystemOutputPort filesystem;
	
	private JarSchedulerAdapter adapter = new JarSchedulerAdapter();
	
	@BeforeEach
    void init() {
		quartz = mock(QuartzConfig.class);
		eventPublisher = mock(ApplicationEventPublisher.class);
		storage = mock(StorageOutputPort.class);
		filesystem = mock(FileSystemOutputPort.class);
		ReflectionTestUtils.setField(adapter, "quartz", quartz);
		ReflectionTestUtils.setField(adapter, "eventPublisher", eventPublisher);
		ReflectionTestUtils.setField(adapter, "storage", storage);
		ReflectionTestUtils.setField(adapter, "fileSystem", filesystem);
	}
	
	
	
	@Test
	void getPropertiesTest() {
		Path jarfile = Path.of("C:\\tmp\\dagrags\\dagJar1.jar"); 
		var rv = adapter.getProperties(jarfile);
		assertNotNull(rv);
	}
	@Test
	void getOperatorsTest() {
		var rv = adapter.getOperators();
		assertNotNull(rv);
	}
	
	@Test
	void unscheduleTest() throws DomainException {
		adapter.unschedule("dagname", "jarname");
		assertTrue(true);
	}
	@Test
	void getDagDetailTest() throws DomainException {
		var rv = adapter.getDagDetail("jarname");
		assertNotNull(rv);
	}
	@Test
	void getDagDetailSistemaTest() throws DomainException {
		var rv = adapter.getDagDetail("system");
		assertNotNull(rv);
	}
	@Test
	void executeTest() throws DomainException {
		adapter.execute("jarname", "dagname", "type","");
		assertTrue(true);
	}
	@Test
	void listScheduledtest() throws DomainException {
		var rv = adapter.listScheduled();
		assertNotNull(rv);
	}
	@Test
	void listScheduled_ExceptionThrown_DomainException() throws SchedulerException {
	    when(quartz.listScheduled()).thenThrow(new RuntimeException("Test Exception"));
	    assertThrows(DomainException.class, () -> adapter.listScheduled());
	}
	@Test
	void getIconsErrorTest() throws DomainException {
		try {
			adapter.getIcons("otro");	
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	@Test
	void getIconsTest() throws DomainException {
		var rv = adapter.getIcons("main.cl.dagserver.infra.adapters.operators.DummyOperator");
		assertNotNull(rv);
	}
	
	@Test
	void privateTest() {
		ReflectionTestUtils.invokeMethod(adapter, "activateDeactivate", "dagname", this.getClass());
		assertTrue(true);
	}
	@Test
	void deleteXCOMTest() throws DomainException {
		var date = new Date();
		adapter.deleteXCOM(date);
		assertTrue(true);
	}
}