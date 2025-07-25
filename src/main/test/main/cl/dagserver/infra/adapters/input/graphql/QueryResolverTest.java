package main.cl.dagserver.infra.adapters.input.graphql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import com.nhl.dflib.DataFrame;
import main.cl.dagserver.application.ports.input.LoginUseCase;
import main.cl.dagserver.application.ports.input.SchedulerQueryUseCase;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.AgentDTO;
import main.cl.dagserver.domain.model.DagDTO;
import main.cl.dagserver.domain.model.LogDTO;
import main.cl.dagserver.domain.model.PropertyDTO;
import main.cl.dagserver.domain.model.SessionDTO;
import main.cl.dagserver.domain.model.UncompiledDTO;
import main.cl.dagserver.domain.model.UserDTO;
import main.cl.dagserver.infra.adapters.input.graphql.types.Scheduled;

class QueryResolverTest {

	private QueryResolver resolver;
	
	@Mock
	SchedulerQueryUseCase handler;
	
	@Mock
	LoginUseCase login;
	
	
	
	@BeforeEach
    void init() {
		handler = mock(SchedulerQueryUseCase.class);
		login = mock(LoginUseCase.class);
		resolver = new QueryResolver(handler,login);
		ReflectionTestUtils.setField(resolver, "handler", handler);
		ReflectionTestUtils.setField(resolver, "login", login);
		
	}
	@Test
	void loginTest() {
		SessionDTO rv = new SessionDTO();
		rv.setToken("test");
		rv.setRefreshToken("refresh");
		when(login.apply(anyString())).thenReturn(rv);
		var test = resolver.login("test");
		assertNotNull(test);
	}
	@Test
	void operatorsMetadata() throws DomainException, JSONException {
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
		Map<String,DataFrame> mapa = new HashMap<>();
		dto.setDagname("test");
		dto.setExecDt(new Date());
		dto.setId(1);
		dto.setOutputxcom("test");
		dto.setStatus("status");
		dto.setValue("value");
		dto.setXcom(mapa);
		List<LogDTO> arr = new ArrayList<>();
		arr.add(dto);
		when(handler.getLogs(anyString())).thenReturn(arr);
		var list = resolver.logs("test");
		assertEquals(arr.size(), list.size());
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
		when(handler.getDagDetail(anyString())).thenThrow(new DomainException(new Exception("test")));
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
	void agentsTest() {
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
	
	@Test
	void exportUncompiledTest() throws DomainException {
		when(handler.exportUncompiled(anyString(),anyInt())).thenReturn("test");
		var str = resolver.exportUncompiled("test",1);
		assertNotNull(str);
	}
	@Test
	void exceptionsTest() throws DomainException {
		when(handler.getExceptions(anyString())).thenReturn(new ArrayList<>());
		var str = resolver.exceptions("test");
		assertNotNull(str);
	}
}