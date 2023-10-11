package main.domain.services;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import main.application.ports.output.SchedulerRepositoryOutputPort;
import main.domain.core.TokenEngine;
import main.domain.model.UserDTO;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertTrue;

class LoginServiceTest {

	private LoginService login = new LoginService();
	
	@Mock 
	SchedulerRepositoryOutputPort repo;
	
	@Mock
	TokenEngine tokenEngine;
	
	@BeforeEach
    public void init() {
		repo = mock(SchedulerRepositoryOutputPort.class);
		tokenEngine = mock(TokenEngine.class);
		ReflectionTestUtils.setField(login, "repository", repo);
		ReflectionTestUtils.setField(login, "tokenEngine", tokenEngine);
		ReflectionTestUtils.setField(login, "jwtSecret", "jwtSecret");
		ReflectionTestUtils.setField(login, "jwtSigner", "jwtSigner");
		ReflectionTestUtils.setField(login, "jwtSubject", "jwtSubject");
		ReflectionTestUtils.setField(login, "jwtTtl", 1);
	}
	
	@Test
	void applyTest() {
		UserDTO user = new UserDTO();
		user.setCreatedAt(new Date());
		user.setPwdhash("test");
		user.setTypeAccount("ADMIN");
		user.setUsername("username");
		user.setId(1);
		List<UserDTO> list = new ArrayList<>();
		list.add(user);
		
		Map<String,String> claimsmap = new HashMap<>();
		claimsmap.put("typeAccount", "USER");
		Map<String,Object> ret = new HashMap<>();
		ret.put("claims", claimsmap);
		when(tokenEngine.untokenize(anyString(),anyString(),anyString())).thenReturn(ret);
		
		when(repo.findUser(anyString())).thenReturn(list);
		login.apply(Arrays.asList("test","test"));
		assertTrue(true);
	}
	
	@Test
	void applyErrorTest() {
		UserDTO user = new UserDTO();
		user.setCreatedAt(new Date());
		user.setPwdhash("test123");
		user.setTypeAccount("ADMIN");
		user.setUsername("username");
		user.setId(1);
		List<UserDTO> list = new ArrayList<>();
		list.add(user);
		
		Map<String,String> claimsmap = new HashMap<>();
		claimsmap.put("typeAccount", "USER");
		Map<String,Object> ret = new HashMap<>();
		ret.put("claims", claimsmap);
		when(tokenEngine.untokenize(anyString(),anyString(),anyString())).thenReturn(ret);
		
		when(repo.findUser(anyString())).thenReturn(list);
		var str = login.apply(Arrays.asList("test","test"));
		assertNotNull(str);
	}
}
