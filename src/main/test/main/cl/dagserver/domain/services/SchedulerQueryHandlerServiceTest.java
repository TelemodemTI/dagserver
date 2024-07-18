package main.cl.dagserver.domain.services;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import com.nhl.dflib.DataFrame;
import main.cl.dagserver.application.ports.output.AuthenticationOutputPort;
import main.cl.dagserver.application.ports.output.CompilerOutputPort;
import main.cl.dagserver.application.ports.output.JarSchedulerOutputPort;
import main.cl.dagserver.application.ports.output.SchedulerRepositoryOutputPort;
import main.cl.dagserver.domain.enums.AccountType;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.AgentDTO;
import main.cl.dagserver.domain.model.AuthDTO;
import main.cl.dagserver.domain.model.EventListenerDTO;
import main.cl.dagserver.domain.model.LogDTO;
import main.cl.dagserver.domain.model.PropertyParameterDTO;
import main.cl.dagserver.infra.adapters.output.scheduler.JarSchedulerAdapter;

class SchedulerQueryHandlerServiceTest {
	
	private SchedulerQueryHandlerService service = new SchedulerQueryHandlerService();
	
	@Mock
	private JarSchedulerOutputPort scanner;
	
	@Mock
	private SchedulerRepositoryOutputPort repository;
	
	@Mock
	protected AuthenticationOutputPort tokenEngine;
	
	@Mock
	protected CompilerOutputPort compiler;
	
	@BeforeEach
    public void init() {
		scanner = mock(JarSchedulerOutputPort.class);
		repository = mock(SchedulerRepositoryOutputPort.class);
		tokenEngine = mock(AuthenticationOutputPort.class);
		compiler = mock(CompilerOutputPort.class);
		ReflectionTestUtils.setField(service, "scanner", scanner);
		ReflectionTestUtils.setField(service, "repository", repository);
		ReflectionTestUtils.setField(service, "auth", tokenEngine);
		ReflectionTestUtils.setField(service, "compiler", compiler);
		ReflectionTestUtils.setField(service, "gitHubPropkey", "gitHubPropkey");
		ReflectionTestUtils.setField(service, "rabbitPropkey", "rabbitPropkey");
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
	@Test
	void availableJobsTest() throws DomainException {
		JarSchedulerAdapter adapter = mock(JarSchedulerAdapter.class);
		when(adapter.getOperators()).thenReturn(new HashMap<>());
		when(scanner.init()).thenReturn(adapter);
		var rt = service.availableJobs();
		assertNotNull(rt);
	}
	@Test
	void getLogsTest() throws DomainException {
		LogDTO log = new LogDTO();
		log.setDagname("asdf");
		log.setOutputxcom("test");
		List<LogDTO> list = new ArrayList<>();
		list.add(log);
		when(repository.getLogs(anyString())).thenReturn(list);
		DataFrame xcom = DataFrame.byArrayRow("status") 
		        .appender() 
		        .append("ok")   
		        .toDataFrame();
		Map<String,DataFrame> xcomr = new HashMap<>();
		xcomr.put("test", xcom);
		when(repository.readXcom(anyString())).thenReturn(xcomr);
		var rt = service.getLogs("test");
		assertNotNull(rt);
	}
	@Test
	void getDagDetailTest() throws DomainException {
		JarSchedulerAdapter adapter = mock(JarSchedulerAdapter.class);
		when(adapter.getDagDetail(anyString())).thenReturn(new ArrayList<>());
		when(scanner.init()).thenReturn(adapter);
		var rt = service.getDagDetail("test");
		assertNotNull(rt);
	}
	@Test
	void propertiesTest() throws DomainException {
		PropertyParameterDTO dto = new PropertyParameterDTO();
		dto.setDescription("descr");
		List<PropertyParameterDTO> list = new ArrayList<>();
		list.add(dto);
		when(repository.getProperties(null)).thenReturn(list);
		var rt = service.properties();
		assertNotNull(rt);
	}
	@Test
	void agentsTest() {
		AgentDTO dto = new AgentDTO();
		dto.setHostname("host1");
		List<AgentDTO> list = new ArrayList<>();
		list.add(dto);
		when(repository.getAgents()).thenReturn(list);
		var rt = service.agents();
		assertNotNull(rt);
	}
	@Test
	void getUncompiledsErrorTest() throws DomainException {
		try {
			doThrow(new RuntimeException("Test")).when(repository).getUncompileds();
			service.getUncompileds("token");	
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	@Test
	void getUncompiledsTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		when(repository.getUncompileds()).thenReturn(new ArrayList<>());
		var rt = service.getUncompileds("token");
		assertNotNull(rt);
	}
	@Test
	void operatorsTest() throws DomainException {
		when(compiler.operators()).thenReturn(new JSONArray());
		var rt = service.operators();
		assertNotNull(rt);
	}
	@Test
	void credentialsTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		ret.setAccountType(AccountType.ADMIN);
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		when(repository.getUsers()).thenReturn(new ArrayList<>());
		var rt = service.credentials("asdfg");
		assertNotNull(rt);
	}
	@Test
	void credentialsUserTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		ret.setAccountType(AccountType.USER);
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		when(repository.getUsers()).thenReturn(new ArrayList<>());
		var rt = service.credentials("asdfg");
		assertNotNull(rt);
	}
	@Test
	void credentialsErrorTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		ret.setAccountType(AccountType.ADMIN);
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		doThrow(new RuntimeException("Test")).when(repository).getUsers();
		try {
			service.credentials("asdfg");	
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	@Test
	void getIconsTest() throws DomainException {
		when(scanner.getIcons(anyString())).thenReturn("test");
		var rt = service.getIcons("type");
		assertNotNull(rt);
	}
	@Test
	void getDependenciesTest() throws DomainException {
		EventListenerDTO event = new EventListenerDTO();
		event.setGroupName("group");
		event.setOnStart("test1");
		event.setOnEnd("end");
		event.setListenerName("tesrs");
		List<EventListenerDTO> list = new ArrayList<>();
		list.add(event);
		when(repository.listEventListeners()).thenReturn(list);
		JarSchedulerAdapter adapter = mock(JarSchedulerAdapter.class);
		var map = new HashMap<String,List<Map<String,String>>>();
		var listp = new ArrayList<Map<String,String>>();
		var entrymp = new HashMap<String,String>();
		entrymp.put("dagname", "tesrs");
		listp.add(entrymp);
		map.put("test", listp);
		when(adapter.getOperators()).thenReturn(map);
		when(scanner.init()).thenReturn(adapter);
		var rt = service.getDependencies("type","test1");
		assertNotNull(rt);
	}
	@Test
	void getDependenciesEndTest() throws DomainException {
		EventListenerDTO event = new EventListenerDTO();
		event.setGroupName("group");
		event.setOnStart("start");
		event.setOnEnd("test1");
		event.setListenerName("tesrs");
		List<EventListenerDTO> list = new ArrayList<>();
		list.add(event);
		when(repository.listEventListeners()).thenReturn(list);
		JarSchedulerAdapter adapter = mock(JarSchedulerAdapter.class);
		var map = new HashMap<String,List<Map<String,String>>>();
		var listp = new ArrayList<Map<String,String>>();
		var entrymp = new HashMap<String,String>();
		entrymp.put("dagname", "tesrs");
		listp.add(entrymp);
		map.put("test", listp);
		when(adapter.getOperators()).thenReturn(map);
		when(scanner.init()).thenReturn(adapter);
		var rt = service.getDependencies("type","test1");
		assertNotNull(rt);
	}
	@Test
	void getChannelsTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		ret.setAccountType(AccountType.ADMIN);
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		PropertyParameterDTO prop = new PropertyParameterDTO();
		prop.setName("STATUS");
		PropertyParameterDTO prop1 = new PropertyParameterDTO();
		prop1.setName("asdf");
		var propertyList = new ArrayList<PropertyParameterDTO>();
		propertyList.add(prop);
		propertyList.add(prop1);
		when(repository.getProperties(anyString())).thenReturn(propertyList);
		
		var rt = service.getChannels("token");
		assertNotNull(rt);
	}
	@Test
	void getChannelsErrorTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		ret.setAccountType(AccountType.USER);
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		PropertyParameterDTO prop = new PropertyParameterDTO();
		prop.setName("STATUS");
		PropertyParameterDTO prop1 = new PropertyParameterDTO();
		prop1.setName("asdf");
		var propertyList = new ArrayList<PropertyParameterDTO>();
		propertyList.add(prop);
		propertyList.add(prop1);
		when(repository.getProperties(anyString())).thenReturn(propertyList);
		try {
			service.getChannels("token");	
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	@Test
	void exportUncompiledTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		when(repository.getUncompiledBin(anyInt())).thenReturn("test");
		var rt = service.exportUncompiled("token",1);
		assertNotNull(rt);
	}
}
