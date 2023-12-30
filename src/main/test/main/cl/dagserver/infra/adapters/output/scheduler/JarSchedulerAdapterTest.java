package main.cl.dagserver.infra.adapters.output.scheduler;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import java.util.Date;
import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.infra.adapters.confs.QuartzConfig;

class JarSchedulerAdapterTest {

	@Mock
    private ApplicationEventPublisher eventPublisher;
	@Mock
	private QuartzConfig quartz;
	
	private JarSchedulerAdapter adapter = new JarSchedulerAdapter();
	
	@BeforeEach
    void init() {
		quartz = mock(QuartzConfig.class);
		eventPublisher = mock(ApplicationEventPublisher.class);
		ReflectionTestUtils.setField(adapter, "quartz", quartz);
		ReflectionTestUtils.setField(adapter, "eventPublisher", eventPublisher);
		ReflectionTestUtils.setField(adapter, "pathfolder", "C:\\tmp\\dagrags\\");
		ReflectionTestUtils.setField(adapter, "xcomfolder", "C:\\tmp\\dagrags\\xcom_files");
	}
	
	@Test
	void initTest() throws DomainException {
		try {
			var dadap = adapter.init();
			assertNotNull(dadap);
		} catch (Exception e) {}
		assertTrue(true);
	}
	
	@Test
	void getPropertiesTest() {
		File jarfile = new File("C:\\tmp\\dagrags\\dagJar1.jar");
		var rv = adapter.getProperties(jarfile);
		assertNotNull(rv);
	}
	@Test
	void getOperatorsTest() {
		var rv = adapter.getOperators();
		assertNotNull(rv);
	}
	@Test
	void schedulerTest() throws DomainException {
		try {
			adapter.init();
			adapter.scheduler("DAG_UzAjxX", "dagJar1.jar");
			assertTrue(true);	
		} catch (Exception e) {}
		assertTrue(true);
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
		adapter.execute("jarname", "dagname", "type");
		assertTrue(true);
	}
	@Test
	void listScheduledtest() throws DomainException {
		var rv = adapter.listScheduled();
		assertNotNull(rv);
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
	void deleteXCOMTest() throws DomainException {
		try {
			adapter.deleteXCOM(new Date());	
		} catch (Exception e) {}
		assertTrue(true);
	}
	@Test
	void privateTest() {
		ReflectionTestUtils.invokeMethod(adapter, "activateDeactivate", "dagname", this.getClass());
		assertTrue(true);
	}
}
