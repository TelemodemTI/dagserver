package main.domain.services;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import main.application.ports.output.SchedulerRepositoryOutputPort;
import main.domain.model.UserDTO;
import static org.mockito.Mockito.when;

class LoginServiceTest {

	private LoginService login = new LoginService();
	
	@Mock 
	SchedulerRepositoryOutputPort repo;
	
	@BeforeEach
    public void init() {
		repo = mock(SchedulerRepositoryOutputPort.class);
		ReflectionTestUtils.setField(login, "repository", repo);
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
		when(repo.findUser(anyString())).thenReturn(list);
		var str = login.apply(Arrays.asList("test","test"));
		assertNotNull(str);
	}
}
