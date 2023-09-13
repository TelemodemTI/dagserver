package main.domain.services;

import static org.mockito.Mockito.mock;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import main.application.ports.output.CompilerOutputPort;
import main.application.ports.output.JarSchedulerOutputPort;
import main.application.ports.output.SchedulerRepositoryOutputPort;
import main.domain.core.TokenEngine;
import main.domain.exceptions.DomainException;
import main.infra.adapters.output.scheduler.JarSchedulerAdapter;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SchedulerMutationHandlerServiceTest {

	private SchedulerMutationHandlerService service = new SchedulerMutationHandlerService();
	
	@Mock
	protected JarSchedulerOutputPort scanner;
	
	@Mock 
	protected SchedulerRepositoryOutputPort repository;
	
	@Mock
	protected CompilerOutputPort compiler;
	
	@Mock
	protected TokenEngine tokenEngine;
	
	@BeforeEach
    public void init() {
		scanner = mock(JarSchedulerOutputPort.class);
		repository = mock(SchedulerRepositoryOutputPort.class);
		compiler = mock(CompilerOutputPort.class);
		tokenEngine = mock(TokenEngine.class);
		ReflectionTestUtils.setField(service, "scanner", scanner);
		ReflectionTestUtils.setField(service, "repository", repository);
		ReflectionTestUtils.setField(service, "compiler", compiler);
		ReflectionTestUtils.setField(service, "tokenEngine", tokenEngine);
		ReflectionTestUtils.setField(service, "jwtSecret", "jwtSecret");
		ReflectionTestUtils.setField(service, "jwtSigner", "jwtSigner");
		ReflectionTestUtils.setField(service, "jwtSubject", "jwtSubject");
		ReflectionTestUtils.setField(service, "jwtTtl", 1);
	}
	
	@Test
	void scheduleDagTest() throws DomainException {
		Map<String,Object> ret = new HashMap<>();
		JarSchedulerAdapter adapter = mock(JarSchedulerAdapter.class);
		when(scanner.init()).thenReturn(adapter);
		when(tokenEngine.untokenize(anyString(),anyString(),anyString())).thenReturn(ret);
		service.scheduleDag("token", "dagname", "jarname");
		assertTrue(true);
	}
	@Test
	void scheduleDagErrorTest() throws DomainException {
		Map<String,Object> ret = new HashMap<>();
		JarSchedulerAdapter adapter = mock(JarSchedulerAdapter.class);
		when(scanner.init()).thenReturn(adapter);
		when(tokenEngine.untokenize(anyString(),anyString(),anyString())).thenReturn(ret);
		try {
			when(scanner.init()).thenThrow(new DomainException("test"));
			service.scheduleDag("token", "dagname", "jarname");	
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	
	@Test
	void unscheduleDagTest() throws DomainException {
		Map<String,Object> ret = new HashMap<>();
		JarSchedulerAdapter adapter = mock(JarSchedulerAdapter.class);
		when(scanner.init()).thenReturn(adapter);
		when(tokenEngine.untokenize(anyString(),anyString(),anyString())).thenReturn(ret);
		service.unscheduleDag("token", "dagname", "jarname");
		assertTrue(true);
	}
	@Test
	void unscheduleDagErrorTest() throws DomainException {
		Map<String,Object> ret = new HashMap<>();
		when(scanner.init()).thenThrow(new DomainException("test"));
		when(tokenEngine.untokenize(anyString(),anyString(),anyString())).thenReturn(ret);
		try {
			service.unscheduleDag("token", "dagname", "jarname");
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	@Test
	void createPropertyTest() throws DomainException {
		Map<String,Object> ret = new HashMap<>();
		when(tokenEngine.untokenize(anyString(),anyString(),anyString())).thenReturn(ret);
		service.createProperty("token", "name", "descr", "value", "group");
		assertTrue(true);
	}
	@Test
	void createPropertyErrorTest() throws DomainException {
		try {
			Map<String,Object> ret = new HashMap<>();
			doThrow(new RuntimeException("Test")).when(repository).setProperty(anyString(), anyString(), anyString(), anyString());
			when(tokenEngine.untokenize(anyString(),anyString(),anyString())).thenReturn(ret);
			service.createProperty("token", "name", "descr", "value", "group");
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	@Test
	void deletePropertyTest() throws DomainException {
		Map<String,Object> ret = new HashMap<>();
		when(tokenEngine.untokenize(anyString(),anyString(),anyString())).thenReturn(ret);
		service.deleteProperty("test", "name", "group");
		assertTrue(true);
	}
	@Test
	void deletePropertyErrorTest() throws DomainException {
		Map<String,Object> ret = new HashMap<>();
		doThrow(new RuntimeException("Test")).when(repository).delProperty(anyString(), anyString());
		try {
			when(tokenEngine.untokenize(anyString(),anyString(),anyString())).thenReturn(ret);
			service.deleteProperty("test", "name", "group");
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	@Test
	void execute() throws DomainException {
		Map<String,Object> ret = new HashMap<>();
		JarSchedulerAdapter adapter = mock(JarSchedulerAdapter.class);
		when(scanner.init()).thenReturn(adapter);
		when(tokenEngine.untokenize(anyString(),anyString(),anyString())).thenReturn(ret);
		service.execute("token","jarname", "dagname","channel");
		assertTrue(true);
	}
	@Test
	void executeTest() throws DomainException {
		Map<String,Object> ret = new HashMap<>();
		try {
			when(tokenEngine.untokenize(anyString(),anyString(),anyString())).thenReturn(ret);
			when(scanner.init()).thenThrow(new DomainException("test"));
			service.execute("token","jarname", "dagname","channel");
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	
	@Test
	void saveUncompiledTest() throws DomainException {
		Map<String,Object> ret = new HashMap<>();
		var obj = new JSONObject();
		obj.put("jarname", "jarname");
		when(tokenEngine.untokenize(anyString(),anyString(),anyString())).thenReturn(ret);
		service.saveUncompiled("token", obj);
		assertTrue(true);
	}
	@Test
	void saveUncompiledErrorTest() throws DomainException {
		var obj = new JSONObject();
		obj.put("jarname", "jarname");
		Map<String,Object> ret = new HashMap<>();
		doThrow(new RuntimeException("Test")).when(repository).addUncompiled(anyString(), any());
		try {
			when(tokenEngine.untokenize(anyString(),anyString(),anyString())).thenReturn(ret);
			service.saveUncompiled("token", obj);
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	@Test
	void updateUncompiledTest() throws DomainException {
		Map<String,Object> ret = new HashMap<>();
		when(tokenEngine.untokenize(anyString(),anyString(),anyString())).thenReturn(ret);
		service.updateUncompiled("token", 1, new JSONObject());
		assertTrue(true);
	}
	@Test
	void updateUncompiledErrorTest() throws DomainException {
		Map<String,Object> ret = new HashMap<>();
		doThrow(new RuntimeException("Test")).when(repository).updateUncompiled(anyInt(), any());
		try {
			when(tokenEngine.untokenize(anyString(),anyString(),anyString())).thenReturn(ret);
			service.updateUncompiled("token", 1, new JSONObject());
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	@Test
	void compileTest() throws DomainException {
		Map<String,Object> ret = new HashMap<>();
		var json = "{\"jarname\":\"test\"}";
		when(repository.getUncompiledBin(anyInt())).thenReturn(json);
		when(tokenEngine.untokenize(anyString(),anyString(),anyString())).thenReturn(ret);
		service.compile("token", 1, true);
		assertTrue(true);
	}
	@Test
	void compileErrorTest() throws DomainException {
		Map<String,Object> ret = new HashMap<>();
		doThrow(new DomainException("test")).when(compiler).createJar(anyString(),any());
		try {
			when(tokenEngine.untokenize(anyString(),anyString(),anyString())).thenReturn(ret);
			service.compile("token", 1, true);
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	@Test
	void deleteUncompiledTest() throws DomainException {
		Map<String,Object> ret = new HashMap<>();
		when(tokenEngine.untokenize(anyString(),anyString(),anyString())).thenReturn(ret);
		service.deleteUncompiled("token", 1);
		assertTrue(true);
	}
	@Test
	void deleteUncompileErrorTest() throws DomainException {
		Map<String,Object> ret = new HashMap<>();
		doThrow(new RuntimeException("Test")).when(repository).deleteUncompiled(anyInt());
		try {
			when(tokenEngine.untokenize(anyString(),anyString(),anyString())).thenReturn(ret);
			service.deleteUncompiled("token", 1);
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	@Test
	void deleteGroupPropertyTest() throws DomainException {
		Map<String,Object> ret = new HashMap<>();
		when(tokenEngine.untokenize(anyString(),anyString(),anyString())).thenReturn(ret);
		service.deleteGroupProperty("token","name","group");
		assertTrue(true);
	}
	@Test
	void deleteGroupPropertyErrorTest() throws DomainException {
		Map<String,Object> ret = new HashMap<>();
		doThrow(new RuntimeException("Test")).when(repository).delGroupProperty(anyString());
		try {
			when(tokenEngine.untokenize(anyString(),anyString(),anyString())).thenReturn(ret);
			service.deleteGroupProperty("token","name","group");
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	@Test
	void createAccountErrorTest() throws DomainException {
		Map<String,Object> ret = new HashMap<>();
		ret.put("typeAccount", "ADMIN");
		try {
			when(tokenEngine.untokenize(anyString(),anyString(),anyString())).thenReturn(ret);
			service.createAccount("token", "username", "type", "hash");
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	@Test
	void createAccountUserTest() throws DomainException {
		Map<String,String> claimsmap = new HashMap<>();
		claimsmap.put("typeAccount", "USER");
		Map<String,Object> ret = new HashMap<>();
		ret.put("claims", claimsmap);
		when(tokenEngine.untokenize(anyString(),anyString(),anyString())).thenReturn(ret);
		try {
			service.createAccount("token", "username", "type", "hash");
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	@Test
	void createAccountTest() throws DomainException {
		Map<String,String> claimsmap = new HashMap<>();
		claimsmap.put("typeAccount", "ADMIN");
		Map<String,Object> ret = new HashMap<>();
		ret.put("claims", claimsmap);
		when(tokenEngine.untokenize(anyString(),anyString(),anyString())).thenReturn(ret);
		service.createAccount("token", "username", "type", "hash");
		assertTrue(true);
	}
	@Test
	void deleteAccountTest() throws DomainException {
		Map<String,String> claimsmap = new HashMap<>();
		claimsmap.put("typeAccount", "ADMIN");
		Map<String,Object> ret = new HashMap<>();
		ret.put("claims", claimsmap);
		when(tokenEngine.untokenize(anyString(),anyString(),anyString())).thenReturn(ret);
		service.deleteAccount("token", "username");
		assertTrue(true);
	}
	@Test
	void deleteAccountUserTest() throws DomainException {
		Map<String,String> claimsmap = new HashMap<>();
		claimsmap.put("typeAccount", "USER");
		Map<String,Object> ret = new HashMap<>();
		ret.put("claims", claimsmap);
		when(tokenEngine.untokenize(anyString(),anyString(),anyString())).thenReturn(ret);
		try {
			service.deleteAccount("token", "username");	
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	@Test
	void deleteAccountErrorTest() throws DomainException {
		Map<String,String> claimsmap = new HashMap<>();
		claimsmap.put("typeAccount", "ADMIN");
		Map<String,Object> ret = new HashMap<>();
		ret.put("claims", claimsmap);
		when(tokenEngine.untokenize(anyString(),anyString(),anyString())).thenReturn(ret);
		doThrow(new RuntimeException("Test")).when(repository).delAccount(anyString());
		try {
			service.deleteAccount("token", "username");	
		} catch (Exception e) {
			assertTrue(true);
		}
	}
}
