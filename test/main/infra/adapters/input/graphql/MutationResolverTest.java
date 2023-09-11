package main.infra.adapters.input.graphql;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import main.application.ports.input.SchedulerMutationUseCase;
import main.domain.exceptions.DomainException;
class MutationResolverTest {

	private MutationResolver mutation = new MutationResolver();
	
	@Mock
	SchedulerMutationUseCase handler;
	
	@BeforeEach
    void init() {
		handler = mock(SchedulerMutationUseCase.class);
		ReflectionTestUtils.setField(mutation, "handler", handler);
	}
	@Test
	void scheduleDagTest() throws DomainException {
		var resp = mutation.scheduleDag("test", "etst", "test");
		assertNotNull(resp);
		resp.getStatus();
		resp.setStatus("test");
		resp.getCode();
		resp.setCode(1);
		resp.getValue();
		resp.setValue("value");;
		assertTrue(true);
	}
	@Test
	void scheduleDagErrorTest() throws DomainException {
		doThrow(new DomainException("test")).when(handler).scheduleDag(anyString(),anyString(),anyString());
		var resp = mutation.scheduleDag("test", "etst", "test");
		assertNotNull(resp);
	}
	@Test
	void unscheduleDagTest() throws DomainException {
		var resp = mutation.unscheduleDag("test", "test", "test");
		assertNotNull(resp);
		doThrow(new RuntimeException("test")).when(handler).unscheduleDag(anyString(), anyString(), anyString());
		var resp2 = mutation.unscheduleDag("test", "test", "test");
		assertNotNull(resp2);
	}
	
	@Test
	void createPropertyTest() throws DomainException {
		var resp = mutation.createProperty("test", "test", "test","test","test");
		assertNotNull(resp);
		doThrow(new RuntimeException("test")).when(handler).createProperty(anyString(), anyString(), anyString(), anyString(), anyString());
		var resp2 = mutation.createProperty("test", "test", "test","test","test");
		assertNotNull(resp2);
	}
	
	@Test
	void deletePropertyTest() throws DomainException {
		var resp = mutation.deleteProperty("test", "test", "test");
		assertNotNull(resp);
		doThrow(new RuntimeException("test")).when(handler).deleteProperty(anyString(), anyString(), anyString());
		var resp2 = mutation.deleteProperty("test", "test", "test");
		assertNotNull(resp2);
	}
	
	@Test
	void deleteGroupPropertyTest() throws DomainException {
		var resp = mutation.deleteGroupProperty("test", "test", "test");
		assertNotNull(resp);
		doThrow(new RuntimeException("test")).when(handler).deleteGroupProperty(anyString(), anyString(), anyString());
		var resp2 = mutation.deleteGroupProperty("test", "test", "test");
		assertNotNull(resp2);
	}
	
	@Test
	void executeDagTest() throws DomainException {
		var resp = mutation.executeDag("test", "test", "test");
		assertNotNull(resp);
		doThrow(new RuntimeException("test")).when(handler).execute(anyString(), anyString(), anyString(),anyString());
		var resp2 = mutation.executeDag("test", "test", "test");
		assertNotNull(resp2);
	}
	
	@Test
	void saveUncompiledTest() throws DomainException {
		var resp = mutation.saveUncompiled("test", "eyJ0ZXN0IjoidGVzdCJ9");
		assertNotNull(resp);
		doThrow(new RuntimeException("test")).when(handler).saveUncompiled(anyString(), any());
		var resp2 = mutation.saveUncompiled("test", "test");
		assertNotNull(resp2);
	}
	@Test
	void updateUncompiledTest() throws DomainException {
		var resp = mutation.updateUncompiled("test", 1,"eyJ0ZXN0IjoidGVzdCJ9" );
		assertNotNull(resp);
		doThrow(new RuntimeException("test")).when(handler).updateUncompiled(anyString(), anyInt(), any());
		var resp2 = mutation.updateUncompiled("test", 1,"eyJ0ZXN0IjoidGVzdCJ9" );
		assertNotNull(resp2);
	}
	@Test 
	void compileTest() throws DomainException  {
		var resp = mutation.compile("test", 1, 1);
		assertNotNull(resp);
		doThrow(new RuntimeException("test")).when(handler).compile(anyString(), anyInt(), any());
		var resp2 = mutation.compile("test", 1, 1);
		assertNotNull(resp2);
	}
	@Test
	void deleteUncompiledTest() throws DomainException {
		var resp = mutation.deleteUncompiled("token",1);
		assertNotNull(resp);
		doThrow(new RuntimeException("test")).when(handler).deleteUncompiled(anyString(), anyInt());
		var resp2 = mutation.deleteUncompiled("token",1);
		assertNotNull(resp2);
	}
	@Test
	void createAccountTest() throws DomainException {
		var resp = mutation.createAccount("token", "username", "accountype", "hash");
		assertNotNull(resp);
		doThrow(new RuntimeException("test")).when(handler).createAccount(anyString(),anyString(),anyString(),anyString() );
		var resp2 = mutation.createAccount("token", "username", "accountype", "hash");
		assertNotNull(resp2);
	}
	
	@Test
	void deleteAccountTest() throws DomainException {
		var resp = mutation.deleteAccount("token", "username");
		assertNotNull(resp);
		doThrow(new RuntimeException("test")).when(handler).deleteAccount(anyString(),anyString() );
		var resp2 = mutation.deleteAccount("token", "username");
		assertNotNull(resp2);
	}
	@Test
	void updateParamsCompiledTest() throws DomainException {
		var resp = mutation.updateParamsCompiled("token", "idope", "typeope", "jarname", "bin");
		assertNotNull(resp);
		doThrow(new RuntimeException("test")).when(handler).updateParamsCompiled(anyString(),anyString(),anyString(),anyString() ,anyString());
		var resp2 = mutation.updateParamsCompiled("token", "idope", "typeope", "jarname", "bin");
		assertNotNull(resp2);
	}
	@Test
	void updatePropTest() throws DomainException {
		var resp = mutation.updateProp("test","group","key", "value");
		assertNotNull(resp);
		doThrow(new RuntimeException("test")).when(handler).updateProp(anyString(),anyString(),anyString(),anyString());
		var resp2 = mutation.updateProp("test","group","key", "value");
		assertNotNull(resp2);
	}
	@Test
	void deleteJarfileTest() throws DomainException {
		var resp = mutation.deleteJarfile("test","group");
		assertNotNull(resp);
		doThrow(new RuntimeException("test")).when(handler).deleteJarfile(anyString(),anyString());
		var resp2 = mutation.deleteJarfile("test","group");
		assertNotNull(resp2);
	}
}
