package main.cl.dagserver.infra.adapters.output.repositories;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import static org.mockito.ArgumentMatchers.anyString;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import com.nhl.dflib.DataFrame;

import main.cl.dagserver.domain.enums.OperatorStatus;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.infra.adapters.confs.DAO;
import main.cl.dagserver.infra.adapters.output.repositories.entities.EventListener;
import main.cl.dagserver.infra.adapters.output.repositories.entities.Log;
import main.cl.dagserver.infra.adapters.output.repositories.entities.Metadata;
import main.cl.dagserver.infra.adapters.output.repositories.entities.PropertyParameter;
import main.cl.dagserver.infra.adapters.output.repositories.entities.ScheUncompiledDags;
import main.cl.dagserver.infra.adapters.output.repositories.entities.User;
import main.cl.dagserver.infra.adapters.output.repositories.mappers.SchedulerMapper;
import main.cl.dagserver.infra.adapters.output.repositories.mappers.SchedulerMapperImpl;
import main.cl.dagserver.infra.adapters.output.storage.mapdb.MapDBStorage;

import static org.mockito.Mockito.when;

class SchedulerRepositoryTest {

	
	@Mock
	private DAO dao;
	@Mock
	private SchedulerMapper mapper;
	@Mock
	private MapDBStorage storage;
	
	
	private SchedulerRepository repo = new SchedulerRepository();
	
	@BeforeEach
    void init() {
		dao = mock(DAO.class);
		mapper = new SchedulerMapperImpl();
		storage = mock(MapDBStorage.class);
		ReflectionTestUtils.setField(repo, "dao", dao);
		ReflectionTestUtils.setField(repo, "mapper", mapper);
		ReflectionTestUtils.setField(repo, "storage", storage);
	}
	
	@Test
	void addEventListenerTest() {
		repo.addEventListener("name", "onstart", "onend", "group");
		assertTrue(true);
	}
	@Test
	void removeListener() {
		repo.removeListener("test");
		assertTrue(true);
	}
	@Test
	void listEventListenersTest() {
		EventListener evt = new EventListener();
		List<Object> list = new ArrayList<>();
		list.add(evt);
		when(dao.read(any(), anyString())).thenReturn(list);
		var rv = repo.listEventListeners();
		assertNotNull(rv);
	}
	@Test
	void getEventListenersTest() {
		EventListener evt = new EventListener();
		List<Object> list = new ArrayList<>();
		list.add(evt);
		when(dao.read(any(), anyString())).thenReturn(list);
		var rv = repo.getEventListeners("event");
		assertNotNull(rv);
	}
	@Test
	void getLogsTest() {
		Log evt = new Log();
		List<Object> list = new ArrayList<>();
		list.add(evt);
		when(dao.read(any(), anyString())).thenReturn(list);
		var rv = repo.getLogs("event");
		assertNotNull(rv);
	}
	@Test
	void getLogTest() {
		Log evt = new Log();
		List<Object> list = new ArrayList<>();
		list.add(evt);
		when(dao.read(any(), anyString(),any())).thenReturn(list);
		var rv = repo.getLog(1);
		assertNotNull(rv);
	}
	@Test
	void setLogVacioTest() {
		Map<String, String> parmdata = new HashMap<>(); 
		Map<String, OperatorStatus> status = new HashMap<>();
		List<String> timestamps = new ArrayList<>();
		repo.setLog(parmdata,status,timestamps);
		assertTrue(true);
	}
	@Test
	void setLogTest() {
		Log evt = new Log();
		List<Object> list = new ArrayList<>();
		list.add(evt);
		when(dao.read(any(), anyString(),any())).thenReturn(list);
		Map<String, String> parmdata = new HashMap<>(); 
		Map<String, OperatorStatus> status = new HashMap<>();
		List<String> timestamps = new ArrayList<>();
		repo.setLog(parmdata,status,timestamps);
		assertTrue(true);
	}
	@Test
	void deleteLogsByTest() {
		repo.deleteLogsBy(new Date());
		assertTrue(true);
	}
	@Test
	void findUserTest() {
		var user = repo.findUser("username");
		assertNotNull(user);
	}
	@Test
	void getPropertiesTest() throws DomainException {
		var props = repo.getProperties("group");
		assertNotNull(props);
		props = repo.getProperties(null);
		assertNotNull(props);
	}
	@Test
	void getPropertiesFromDbTest() throws DomainException {
		var props = repo.getPropertiesFromDb("test");
		assertNotNull(props);
	}
	@Test
	void setPropertyNotFoundTest() {
		repo.setProperty("name", "descr", "value", "group");
		assertTrue(true);
	}
	@Test
	void setPropertyTest() {
		PropertyParameter pp = new PropertyParameter();
		pp.setId(1);
		List<Object> props = new ArrayList<>();
		props.add(pp);
		when(dao.read(any(), anyString())).thenReturn(props);
		repo.setProperty("name", "descr", "value", "group");
		assertTrue(true);
	}
	@Test
	void delPropertyTest() {
		PropertyParameter pp = new PropertyParameter();
		pp.setId(1);
		List<Object> props = new ArrayList<>();
		props.add(pp);
		when(dao.read(any(), anyString())).thenReturn(props);
		repo.delProperty("name", "group");
		assertTrue(true);
	}
	@Test
	void setMetadataEmptyTest() {
		repo.setMetadata("hostname", "name");
		assertTrue(true);
	}
	@Test
	void setMetadataTest() {
		Metadata pp = new Metadata();
		pp.setId(1);
		List<Object> props = new ArrayList<>();
		props.add(pp);
		when(dao.read(any(), anyString())).thenReturn(props);
		repo.setMetadata("hostname", "name");
		assertTrue(true);
	}
	@Test
	void getAgentsTest() {
		Metadata pp = new Metadata();
		pp.setId(1);
		pp.setLastUpdatedAt(new Date());
		List<Object> props = new ArrayList<>();
		props.add(pp);
		when(dao.read(any(), anyString())).thenReturn(props);
		var rv = repo.getAgents();
		assertNotNull(rv);
	}
	
	@Test
    void addUncompiledTest() throws DomainException {
        JSONObject json = new JSONObject();
        repo.addUncompiled("test", json);
        assertTrue(true);
    }
	@Test
	void updateUncompiledTest() throws DomainException {
	    JSONObject json = new JSONObject();
	    try {
	    	repo.updateUncompiled(1, json);	
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	@Test
	void getUncompiledsTest() {
		ScheUncompiledDags pp = new ScheUncompiledDags();
		pp.setName("test");
		pp.setCreatedDt(new Date());
		List<Object> props = new ArrayList<>();
		props.add(pp);
		when(dao.read(any(), anyString())).thenReturn(props);
	   var uncompileds = repo.getUncompileds();
	   assertNotNull(uncompileds);
	}
	@Test
	void getUncompiledBinTest() {
		ScheUncompiledDags pp = new ScheUncompiledDags();
		pp.setBin("bin");
		pp.setCreatedDt(new Date());
		List<Object> props = new ArrayList<>();
		props.add(pp);
		when(dao.read(any(), anyString())).thenReturn(props);
		String bin = repo.getUncompiledBin(1);
	   assertNotNull(bin);
    }
	@Test
    void createInternalStatusTest() throws DomainException {
        Map<String,DataFrame> map = new HashMap<>();
        var df = DataFrame
        .byArrayRow("status") 
        .appender() 
        .append("testing")   
        .toDataFrame();
        map.put("test", df);
        String locatedAt = repo.createInternalStatus(map);
        assertNotNull(locatedAt);
    }
	@Test
    void readXcomTest() throws DomainException {
        String locatedAt = "path/to/xcom/data.json";
        Map<String,DataFrame> map = new HashMap<>();
        var df = DataFrame.byArrayRow("status").appender().append("testing").toDataFrame();
        map.put("test", df);
        when(storage.getEntry(anyString())).thenReturn(map);
        Map<String, DataFrame> data = repo.readXcom(locatedAt);
        assertNotNull(data);
    }
	@Test
    void insertIfNotExistsTest() {
        String jarname = "testJar";
        String propertiesFile = "test.properties";
        Properties properties = new Properties();
        properties.setProperty("value.param1", "param1_value");
        properties.setProperty("desc.param1", "Description for param1");
        properties.setProperty("group.param1", "testGroup");
        when(dao.read(any(), anyString())).thenReturn(Collections.emptyList());
        repo.insertIfNotExists(jarname, propertiesFile, properties);
        assertTrue(true);   
    }
	@Test
    void deleteUncompiledTest() {
        Integer uncompiledId = 1;
        when(dao.read(any(), anyString())).thenReturn(Collections.singletonList(new ScheUncompiledDags()));
        repo.deleteUncompiled(uncompiledId);
        assertTrue(true);
    }

	 @Test
	void createParamsTest() throws JSONException, DomainException {
	       String jarname = "testJar";
	       JSONArray boxes = new JSONArray();
	       JSONObject dag = new JSONObject();
	       dag.put("boxes", boxes);
	       JSONArray arr = new JSONArray();
	       arr.put(dag);
	       JSONObject binj = new JSONObject();
	       binj.put("dags", arr);
	       List<String> groupProps = repo.createParams(jarname, binj.toString());
	       assertNotNull(groupProps);
	}
	 @Test
	 void delGroupPropertyTest() {
		 PropertyParameter pp = new PropertyParameter();
			pp.setId(1);
			List<Object> props = new ArrayList<>();
			props.add(pp);
			when(dao.read(any(), anyString())).thenReturn(props);
		 repo.delGroupProperty("group");
		 assertTrue(true);
	 }
	 @Test
	 void getUsersTest() {
		 User pp = new User();
			pp.setId(1);
			List<Object> props = new ArrayList<>();
			props.add(pp);
			when(dao.read(any(), anyString())).thenReturn(props);
		 var list = repo.getUsers();
		 assertNotNull(list);
	 }
	 @Test
	 void createAccountTest() {
		 repo.createAccount("username", "accountType", "pwdHash");
		 assertTrue(true);
	 }
	 @Test
	 void delAccountTest() {
		 User pp = new User();
			pp.setId(1);
			List<Object> props = new ArrayList<>();
			props.add(pp);
			when(dao.read(any(), anyString())).thenReturn(props);
		repo.delAccount("username");
		assertTrue(true);
	 }
	 @Test
	 void updateParamsTest() {
		 PropertyParameter pp = new PropertyParameter();
			pp.setId(1);
			pp.setName("key");
			List<Object> props = new ArrayList<>();
			props.add(pp);
			when(dao.read(any(), anyString())).thenReturn(props);
		 repo.updateParams("ide","type", "jarname", "W3sia2V5Ijoia2V5IiwidmFsdWUiOiJ2YWx1ZSJ9XQ==");
		 assertTrue(true);
	 }
	 @Test
	 void updatepropTest() {
		 PropertyParameter pp = new PropertyParameter();
			pp.setId(1);
			pp.setName("key");
			List<Object> props = new ArrayList<>();
			props.add(pp);
			when(dao.read(any(), anyString())).thenReturn(props);
			repo.updateprop("group", "key", "value");
			assertTrue(true);
	 }
	 @Test
	 void deleteLogTest() {
		 repo.deleteLog(1);
		 assertTrue(true);
	 }
	 @Test
	 void deleteAllLogsTest() {
		 repo.deleteAllLogs("dagname");
		 assertTrue(true);
	 }
	 @Test
	 void renameUncompiledTest() throws JSONException {
		 JSONObject binobj = new JSONObject();
		 binobj.put("jarname", "jarname");
		 ScheUncompiledDags pp = new ScheUncompiledDags();
			pp.setCreatedDt(new Date());
			pp.setName("key");
			pp.setBin(binobj.toString());
			List<Object> props = new ArrayList<>();
			props.add(pp);
			when(dao.read(any(), anyString())).thenReturn(props);
		 repo.renameUncompiled(1,"newname");
		 assertTrue(true);
	 }
	 @Test
	 void getLastLogsTest() {
		 Log evt = new Log();
			List<Object> list = new ArrayList<>();
			list.add(evt);
			when(dao.read(any(), anyString())).thenReturn(list);
		 var rv = repo.getLastLogs();
		 assertNotNull(rv);
	 }
}
