package main.cl.dagserver.domain.services;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;

import main.cl.dagserver.application.ports.output.AuthenticationOutputPort;
import main.cl.dagserver.application.ports.output.SchedulerRepositoryOutputPort;
import main.cl.dagserver.domain.enums.AccountType;
import main.cl.dagserver.domain.model.AuthDTO;
import main.cl.dagserver.domain.model.UserDTO;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertTrue;

class LoginServiceTest {

	private LoginService login;
	
	@Mock 
	SchedulerRepositoryOutputPort repo;
	
	@Mock
	AuthenticationOutputPort tokenEngine;
	
	@BeforeEach
    public void init() {
		repo = mock(SchedulerRepositoryOutputPort.class);
		tokenEngine = mock(AuthenticationOutputPort.class);
		login = new LoginService();
		ReflectionTestUtils.setField(login, "auth", tokenEngine);
	}
	
	@Test
	void applyTest() {
		UserDTO user = new UserDTO();
		user.setCreatedAt(new Date());
		user.setPwdhash("test");
		user.setTypeAccount(AccountType.ADMIN);
		user.setUsername("username");
		user.setId(1);
		List<UserDTO> list = new ArrayList<>();
		list.add(user);

		AuthDTO claims = new AuthDTO();
		claims.setAccountType(AccountType.USER);
		when(tokenEngine.untokenize(anyString())).thenReturn(claims);
		
		when(repo.findUser(anyString())).thenReturn(list);
		login.apply("test");
		assertTrue(true);
	}
	
	@Test
	void applyErrorTest() {
		UserDTO user = new UserDTO();
		user.setCreatedAt(new Date());
		user.setPwdhash("test123");
		user.setTypeAccount(AccountType.ADMIN);
		user.setUsername("username");
		user.setId(1);
		List<UserDTO> list = new ArrayList<>();
		list.add(user);
		
		AuthDTO claims = new AuthDTO();
		claims.setAccountType(AccountType.USER);
		when(tokenEngine.untokenize(anyString())).thenReturn(claims);
		
		when(repo.findUser(anyString())).thenReturn(list);
		var str = login.apply("test");
		assertNotNull(str);
	}
}
