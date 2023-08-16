package main.infra.adapters.input.graphql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import main.application.ports.input.LoginUseCase;
import main.application.ports.input.SchedulerQueryUseCase;
import main.domain.exceptions.DomainException;
import main.domain.model.AgentDTO;
import main.domain.model.DagDTO;
import main.domain.model.LogDTO;
import main.domain.model.PropertyDTO;
import main.domain.model.UncompiledDTO;
import main.domain.model.UserDTO;
import main.infra.adapters.input.graphql.mappers.QueryResolverMapper;
import main.infra.adapters.input.graphql.types.Scheduled;

class QueryResolverTest {

	private QueryResolver resolver = new QueryResolver();
	
	@Mock
	SchedulerQueryUseCase handler;
	
	@Mock
	LoginUseCase login;
	
	@Mock
	QueryResolverMapper mapper;
	
	@BeforeEach
    public void init() {
		handler = mock(SchedulerQueryUseCase.class);
		login = mock(LoginUseCase.class);
		mapper = mock(QueryResolverMapper.class);
		ReflectionTestUtils.setField(resolver, "handler", handler);
		ReflectionTestUtils.setField(resolver, "login", login);
		ReflectionTestUtils.setField(resolver, "mapper", mapper);
	}
	@Test
	void loginTest() {
		when(login.apply(anyList())).thenReturn("test");
		var test = resolver.login("test", "test");
		assertEquals("test", test);
	}
	@Test
	void operatorsMetadata() throws DomainException {
		JSONArray arr = new JSONArray();
		arr.put("test");
		when(handler.operators()).thenReturn(arr);
		var str = resolver.operatorsMetadata();
		JSONArray dos = new JSONArray(str);
		assertEquals(dos.get(0), arr.get(0));
	}
	@Test
	void scheduledJobsTest() throws DomainException {
		Map<String, Object> map = new HashMap<>();
		map.put("jobgroup", "jobgroup");
		map.put("jobname", "jobname");
		map.put("nextFireAt", new Date());
		map.put("eventTrigger", "eventTrigger");
		List<Map<String, Object>> list = new ArrayList<>();
		list.add(map);
		when(handler.listScheduledJobs()).thenReturn(list);
		List<Scheduled> listed = resolver.scheduledJobs();
		assertEquals(list.size(), listed.size());
	}
	@Test
	void scheduledJobsTestNull() throws DomainException {
		Map<String, Object> map = new HashMap<>();
		map.put("jobgroup", "jobgroup");
		map.put("jobname", "jobname");
		map.put("nextFireAt", null);
		map.put("eventTrigger", "eventTrigger");
		List<Map<String, Object>> list = new ArrayList<>();
		list.add(map);
		when(handler.listScheduledJobs()).thenReturn(list);
		List<Scheduled> listed = resolver.scheduledJobs();
		assertEquals(list.size(), listed.size());
	}
	@Test
	void availableJobsTest() throws DomainException {
		Map<String, String> mapa = new HashMap<>();
		mapa.put("classname","classmap");
		mapa.put("cronExpr","cronExpr");
		mapa.put("groupname","groupname");
		mapa.put("dagname","dagname");
		List<Map<String, String>> lista = new ArrayList<>();
		lista.add(mapa);
		Map<String, List<Map<String, String>>> operators = new HashMap<>();
		operators.put("jarname.jar", lista);
		when(handler.availableJobs()).thenReturn(operators);
		var returned = resolver.availableJobs();
		assertNotNull(returned);
	}
	@Test
	void logsTest() throws DomainException {
		LogDTO dto = new LogDTO();
		dto.setDagname("test");
		dto.setExecDt(new Date());
		dto.setId(1);
		dto.setOutputxcom("test");
		dto.setStatus("status");
		dto.setValue("value");
		List<LogDTO> arr = new ArrayList<>();
		arr.add(dto);
		when(handler.getLogs(anyString())).thenReturn(arr);
		var list = resolver.logs("test");
		assertEquals(arr.size(), list.size());
	}
	@Test
	void logsErrorTest() throws DomainException {
		
		when(handler.getLogs(anyString())).thenThrow(new DomainException("test"));
		var list = resolver.logs("test");
		assertNotNull(list);
	}
	@Test
	void detailTeest() throws DomainException {
		List<String> opts = new ArrayList<>();
		opts.add("testing");
		DagDTO dto = new DagDTO();
		dto.setCronExpr("cron");
		dto.setDagname("dagname");
		dto.setGroup("group");
		dto.setOnEnd("onend");
		dto.setOnStart("onstart");
		dto.setOps(Arrays.asList(opts));
		List<DagDTO> map = new ArrayList<>();
		map.add(dto);
		when(handler.getDagDetail(anyString())).thenReturn(map);
		var ri = resolver.detail("test");
		assertNotNull(ri);
	}
	@Test
	void detailErrorTest() throws DomainException {
		List<String> opts = new ArrayList<>();
		opts.add("testing");
		DagDTO dto = new DagDTO();
		dto.setCronExpr("cron");
		dto.setDagname("dagname");
		dto.setGroup("group");
		dto.setOnEnd("onend");
		dto.setOnStart("onstart");
		dto.setOps(Arrays.asList(opts));
		List<DagDTO> map = new ArrayList<>();
		map.add(dto);
		when(handler.getDagDetail(anyString())).thenThrow(new DomainException("test"));
		var ri = resolver.detail("test");
		assertNotNull(ri);
	}
	@Test
	void propertiesTest() throws DomainException {
		PropertyDTO prop = new PropertyDTO();
		List<PropertyDTO> arr = new ArrayList<>();
		arr.add(prop);
		when(handler.properties()).thenReturn(arr);
		var list = resolver.properties();
		assertEquals(arr.size(),list.size());
	}
	@Test
	void agentsTest() throws DomainException {
		AgentDTO prop = new AgentDTO();
		List<AgentDTO> arr = new ArrayList<>();
		arr.add(prop);
		when(handler.agents()).thenReturn(arr);
		var list = resolver.agents();
		assertEquals(arr.size(),list.size());
	}
	@Test
	void uncompiledsTest() throws DomainException {
		UncompiledDTO prop = new UncompiledDTO();
		List<UncompiledDTO> arr = new ArrayList<>();
		arr.add(prop);
		when(handler.getUncompileds("test")).thenReturn(arr);
		var list = resolver.getUncompileds("test");
		assertEquals(arr.size(),list.size());
	}
	@Test
	void credentialsTest() throws DomainException {
		UserDTO prop = new UserDTO();
		List<UserDTO> arr = new ArrayList<>();
		arr.add(prop);
		when(handler.credentials("test")).thenReturn(arr);
		var list = resolver.credentials("test");
		assertEquals(arr.size(),list.size());
	}
	@Test
	void getIconsTest() throws DomainException {
		when(handler.getIcons(anyString())).thenReturn("test");
		var str = resolver.getIcons("test");
		assertNotNull(str);
	}
	@Test
	void getIconsErrorTest() throws DomainException {
		when(handler.getIcons(anyString())).thenThrow(new DomainException("test"));
		var str = resolver.getIcons("test");
		assertNotNull(str);
	}
	@Test
	void getDependenciesTest() throws DomainException {
		List<String> list = new ArrayList<>();
		list.add("testest");
		List<String> list2 = new ArrayList<>();
		list2.add("testest");
		List<List<String>> arl = new ArrayList<>();
		arl.add(list);
		arl.add(list2);
		when(handler.getDependencies(anyString(), anyString())).thenReturn(arl);
		var returned = resolver.getDependencies("test", "test");
		assertNotNull(returned);
	}
}
