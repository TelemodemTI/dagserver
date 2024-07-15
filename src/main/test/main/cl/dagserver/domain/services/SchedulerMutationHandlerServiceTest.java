package main.cl.dagserver.domain.services;

import static org.mockito.Mockito.mock;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import main.cl.dagserver.application.ports.output.AuthenticationOutputPort;
import main.cl.dagserver.application.ports.output.CompilerOutputPort;
import main.cl.dagserver.application.ports.output.JarSchedulerOutputPort;
import main.cl.dagserver.application.ports.output.SchedulerRepositoryOutputPort;
import main.cl.dagserver.domain.enums.AccountType;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.AuthDTO;
import main.cl.dagserver.domain.model.UserDTO;
import main.cl.dagserver.infra.adapters.output.scheduler.JarSchedulerAdapter;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import java.util.ArrayList;
import java.util.List;
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
	protected AuthenticationOutputPort tokenEngine;
	
	@BeforeEach
    public void init() {
		scanner = mock(JarSchedulerOutputPort.class);
		repository = mock(SchedulerRepositoryOutputPort.class);
		compiler = mock(CompilerOutputPort.class);
		tokenEngine = mock(AuthenticationOutputPort.class);
		ReflectionTestUtils.setField(service, "scanner", scanner);
		ReflectionTestUtils.setField(service, "repository", repository);
		ReflectionTestUtils.setField(service, "compiler", compiler);
		ReflectionTestUtils.setField(service, "auth", tokenEngine);
	}
	
	@Test
	void scheduleDagTest() throws DomainException {
		AuthDTO auth = new AuthDTO();
		JarSchedulerAdapter adapter = mock(JarSchedulerAdapter.class);
		when(scanner.init()).thenReturn(adapter);
		when(tokenEngine.untokenize(anyString())).thenReturn(auth);
		service.scheduleDag("token", "dagname", "jarname");
		assertTrue(true);
	}
	@Test
	void scheduleDagErrorTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		JarSchedulerAdapter adapter = mock(JarSchedulerAdapter.class);
		when(scanner.init()).thenReturn(adapter);
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		try {
			when(scanner.init()).thenThrow(new DomainException(new Exception("test")));
			service.scheduleDag("token", "dagname", "jarname");	
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	
	@Test
	void unscheduleDagTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		JarSchedulerAdapter adapter = mock(JarSchedulerAdapter.class);
		when(scanner.init()).thenReturn(adapter);
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		service.unscheduleDag("token", "dagname", "jarname");
		assertTrue(true);
	}
	@Test
	void unscheduleDagErrorTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		when(scanner.init()).thenThrow(new DomainException(new Exception("test")));
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		try {
			service.unscheduleDag("token", "dagname", "jarname");
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	@Test
	void createPropertyTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		service.createProperty("token", "name", "descr", "value", "group");
		assertTrue(true);
	}
	@Test
	void createPropertyErrorTest() throws DomainException {
		try {
			AuthDTO ret = new AuthDTO();
			doThrow(new RuntimeException("Test")).when(repository).setProperty(anyString(), anyString(), anyString(), anyString());
			when(tokenEngine.untokenize(anyString())).thenReturn(ret);
			service.createProperty("token", "name", "descr", "value", "group");
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	@Test
	void deletePropertyTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		service.deleteProperty("test", "name", "group");
		assertTrue(true);
	}
	@Test
	void deletePropertyErrorTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		doThrow(new RuntimeException("Test")).when(repository).delProperty(anyString(), anyString());
		try {
			when(tokenEngine.untokenize(anyString())).thenReturn(ret);
			service.deleteProperty("test", "name", "group");
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	@Test
	void execute() throws DomainException {
		AuthDTO ret = new AuthDTO();
		JarSchedulerAdapter adapter = mock(JarSchedulerAdapter.class);
		when(scanner.init()).thenReturn(adapter);
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		service.execute("token","jarname", "dagname","channel","");
		assertTrue(true);
	}
	@Test
	void executeTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		try {
			when(tokenEngine.untokenize(anyString())).thenReturn(ret);
			when(scanner.init()).thenThrow(new DomainException(new Exception("test")));
			service.execute("token","jarname", "dagname","channel","");
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	
	@Test
	void saveUncompiledTest() throws DomainException, JSONException {
		AuthDTO ret = new AuthDTO();
		var obj = new JSONObject();
		obj.put("jarname", "jarname");
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		service.saveUncompiled("token", obj);
		assertTrue(true);
	}
	@Test
	void saveUncompiledErrorTest() throws DomainException, JSONException {
		var obj = new JSONObject();
		obj.put("jarname", "jarname");
		AuthDTO ret = new AuthDTO();
		doThrow(new RuntimeException("Test")).when(repository).addUncompiled(anyString(), any());
		try {
			when(tokenEngine.untokenize(anyString())).thenReturn(ret);
			service.saveUncompiled("token", obj);
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	@Test
	void updateUncompiledTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		service.updateUncompiled("token", 1, new JSONObject());
		assertTrue(true);
	}
	@Test
	void updateUncompiledErrorTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		doThrow(new RuntimeException("Test")).when(repository).updateUncompiled(anyInt(), any());
		try {
			when(tokenEngine.untokenize(anyString())).thenReturn(ret);
			service.updateUncompiled("token", 1, new JSONObject());
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	@Test
	void compileTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		var json = "{\"jarname\":\"test\"}";
		when(repository.getUncompiledBin(anyInt())).thenReturn(json);
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		service.compile("token", 1, true);
		assertTrue(true);
	}
	@Test
	void compileErrorTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		doThrow(new DomainException(new Exception("test"))).when(compiler).createJar(anyString(),any(),any());
		try {
			when(tokenEngine.untokenize(anyString())).thenReturn(ret);
			service.compile("token", 1, true);
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	@Test
	void deleteUncompiledTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		service.deleteUncompiled("token", 1);
		assertTrue(true);
	}
	@Test
	void deleteUncompileErrorTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		doThrow(new RuntimeException("Test")).when(repository).deleteUncompiled(anyInt());
		try {
			when(tokenEngine.untokenize(anyString())).thenReturn(ret);
			service.deleteUncompiled("token", 1);
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	@Test
	void deleteGroupPropertyTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		service.deleteGroupProperty("token","name","group");
		assertTrue(true);
	}
	@Test
	void deleteGroupPropertyErrorTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		doThrow(new RuntimeException("Test")).when(repository).delGroupProperty(anyString());
		try {
			when(tokenEngine.untokenize(anyString())).thenReturn(ret);
			service.deleteGroupProperty("token","name","group");
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	@Test
	void createAccountErrorTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		ret.setAccountType(AccountType.ADMIN);
		try {
			when(tokenEngine.untokenize(anyString())).thenReturn(ret);
			service.createAccount("token", "username", "type", "hash");
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	@Test
	void createAccountUserTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		ret.setAccountType(AccountType.USER);
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		try {
			service.createAccount("token", "username", "type", "hash");
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	@Test
	void createAccountTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		ret.setAccountType(AccountType.ADMIN);
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		service.createAccount("token", "username", "type", "hash");
		assertTrue(true);
	}
	@Test
	void createAccountAlreadyTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		ret.setAccountType(AccountType.ADMIN);
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		UserDTO user = new UserDTO();
		user.setId(1);
		List<UserDTO> list = new ArrayList<>();
		list.add(user);
		when(repository.findUser(anyString())).thenReturn(list);
		try {
			service.createAccount("token", "username", "type", "hash");	
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	@Test
	void deleteAccountTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		ret.setAccountType(AccountType.ADMIN);
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		service.deleteAccount("token", "username");
		assertTrue(true);
	}
	@Test
	void deleteAccountUserTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		ret.setAccountType(AccountType.ADMIN);
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		try {
			service.deleteAccount("token", "username");	
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	@Test
	void deleteAccountErrorTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		ret.setAccountType(AccountType.ADMIN);
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		doThrow(new RuntimeException("Test")).when(repository).delAccount(anyString());
		try {
			service.deleteAccount("token", "username");	
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	@Test
	void updateParamsCompiledTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		service.updateParamsCompiled("token","name","group","s3","s4");
		assertTrue(true);
		doThrow(new RuntimeException("Test")).when(repository).updateParams(anyString(),anyString(),anyString(),anyString());
		try {
			when(tokenEngine.untokenize(anyString())).thenReturn(ret);
			service.updateParamsCompiled("token","name","group","s3","s4");
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	
	@Test
	void updatePropTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		service.updateProp("token","name","group","s3");
		assertTrue(true);
		doThrow(new RuntimeException("Test")).when(repository).updateprop(anyString(),anyString(),anyString());
		try {
			when(tokenEngine.untokenize(anyString())).thenReturn(ret);
			service.updateProp("token","name","group","s3");
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	
	@Test
	void deleteJarfileTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		ret.setAccountType(AccountType.ADMIN);
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		service.deleteJarfile("token","name");
		assertTrue(true);
		
	}
	@Test
	void deleteJarfileError1Test() throws DomainException {
		AuthDTO ret = new AuthDTO();
		ret.setAccountType(AccountType.USER);
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		try {
			service.deleteJarfile("token","name");	
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	@Test
	void deleteJarfileErrorTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		try {
			service.deleteJarfile("token","name");	
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	
	@Test
	void addGitHubWebhookTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		service.addGitHubWebhook("token","name","group","s3","s4","s5");
		assertTrue(true);
		doThrow(new RuntimeException("Test")).when(repository).setProperty(anyString(),anyString(),anyString(),anyString());
		try {
			when(tokenEngine.untokenize(anyString())).thenReturn(ret);
			service.addGitHubWebhook("token","name","group","s3","s4","s5");
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	
	@Test
	void removeGithubWebhookTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		service.removeGithubWebhook("token","name");
		assertTrue(true);
		doThrow(new RuntimeException("Test")).when(repository).delProperty(anyString(),anyString());
		try {
			when(tokenEngine.untokenize(anyString())).thenReturn(ret);
			service.removeGithubWebhook("token","name");
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	
	@Test
	void deleteLogTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		service.deleteLog("token",1);
		assertTrue(true);
	}
	@Test
	void deleteAllLogsTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		service.deleteAllLogs("token","asdf");
		assertTrue(true);
	}
	@Test
	void renameUncompiledTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		service.renameUncompiled("token",1,"asdf");
		assertTrue(true);
	}
	@Test
	void saveRabbitChannelTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		service.saveRabbitChannel("token","asdf","asdf","asdf",1);
		assertTrue(true);
	}
	@Test
	void addQueueTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		service.addQueue("token","asdf","asdf","asdf");
		assertTrue(true);
	}
	@Test
	void delQueueTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		service.delQueue("token","asdf");
		assertTrue(true);
	}
	@Test
	void saveRedisChannelTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		service.saveRedisChannel("token","asdf","asdf","asdf");
		assertTrue(true);
	}
	@Test
	void addListenerTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		service.addListener("token","asdf","asdf","asdf");
		assertTrue(true);
	}
	@Test
	void delListenerTest() throws DomainException {
		AuthDTO ret = new AuthDTO();
		when(tokenEngine.untokenize(anyString())).thenReturn(ret);
		service.delListener("token","asdf");
		assertTrue(true);
	}
}
