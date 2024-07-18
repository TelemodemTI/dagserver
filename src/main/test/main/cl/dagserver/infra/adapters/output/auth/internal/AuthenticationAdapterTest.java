package main.cl.dagserver.infra.adapters.output.auth.internal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;
import main.cl.dagserver.application.ports.output.SchedulerRepositoryOutputPort;
import main.cl.dagserver.domain.enums.AccountType;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.UserDTO;

class AuthenticationAdapterTest {

    @Mock
    private SchedulerRepositoryOutputPort repository;

    private AuthenticationAdapter adapter;

    @Value("${param.jwt_secret}")
    private String jwtSecret = "testSecret";
    @Value("${param.jwt_signer}")
    private String jwtSigner = "testSigner";
    @Value("${param.jwt_subject}")
    private String jwtSubject = "testSubject";
    @Value("${param.jwt_ttl}")
    private Integer jwtTtl = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        repository = mock(SchedulerRepositoryOutputPort.class);
        adapter = new AuthenticationAdapter(repository);
        ReflectionTestUtils.setField(adapter, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(adapter, "jwtSigner", jwtSigner);
        ReflectionTestUtils.setField(adapter, "jwtSubject", jwtSubject);
        ReflectionTestUtils.setField(adapter, "jwtTtl", jwtTtl);
    }
    @Test
    void loginOKTest() throws DomainException, JSONException {
        JSONObject reqObject = new JSONObject();
        reqObject.put("username", "testUser");
        reqObject.put("private_key", "testPrivateKey");
        reqObject.put("challenge", "testChallenge");
        reqObject.put("blind_signature", "e29b0c4e177ed7b2b70f45fd0b3961655de336b85bcac5fdb1e7e4e16ee168b9");
        UserDTO userDTO = new UserDTO();
        userDTO.setPwdhash("testPwdHash");
        userDTO.setTypeAccount(AccountType.ADMIN);
        userDTO.setId(1);
        when(repository.findUser("testUser")).thenReturn(List.of(userDTO));
        var token = adapter.login(reqObject);
        assertNotNull(token);
    }
    @Test
    void loginNOKTest() throws DomainException, JSONException {
        JSONObject reqObject = new JSONObject();
        reqObject.put("username", "testUser");
        reqObject.put("private_key", "testPrivateKey");
        reqObject.put("challenge", "testChallenge");
        reqObject.put("blind_signature", "123");
        UserDTO userDTO = new UserDTO();
        userDTO.setPwdhash("testPwdHash");
        userDTO.setTypeAccount(AccountType.ADMIN);
        userDTO.setId(1);
        when(repository.findUser("testUser")).thenReturn(List.of(userDTO));
        var token = adapter.login(reqObject);
        assertNotNull(token);
    }
    @Test
    void loginNOKEmptyTest() throws DomainException, JSONException {
        JSONObject reqObject = new JSONObject();
        reqObject.put("username", "testUser");
        reqObject.put("private_key", "testPrivateKey");
        reqObject.put("challenge", "testChallenge");
        reqObject.put("blind_signature", "123");
        
        when(repository.findUser(anyString())).thenReturn(new ArrayList<>());
        var token = adapter.login(reqObject);
        assertNotNull(token);
    }
    @Test
    void logoutTest() {
    	adapter.logout("test");
    	assertTrue(true);
    }
    @Test
    void untokenizeTest() throws JSONException, DomainException {
    	JSONObject reqObject = new JSONObject();
        reqObject.put("username", "testUser");
        reqObject.put("private_key", "testPrivateKey");
        reqObject.put("challenge", "testChallenge");
        reqObject.put("blind_signature", "e29b0c4e177ed7b2b70f45fd0b3961655de336b85bcac5fdb1e7e4e16ee168b9");
        UserDTO userDTO = new UserDTO();
        userDTO.setPwdhash("testPwdHash");
        userDTO.setTypeAccount(AccountType.ADMIN);
        userDTO.setId(1);
        when(repository.findUser("testUser")).thenReturn(List.of(userDTO));
        var token = adapter.login(reqObject);
    	adapter.untokenize(token.getToken());
    	assertTrue(true);
    }
}
