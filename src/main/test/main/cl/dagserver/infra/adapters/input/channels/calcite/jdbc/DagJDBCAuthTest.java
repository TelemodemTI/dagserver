package main.cl.dagserver.infra.adapters.input.channels.calcite.jdbc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class DagJDBCAuthTest {

    private DagJDBCAuth dagJDBCAuth;

    @Mock
    private HttpURLConnection mockConnection;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dagJDBCAuth = new DagJDBCAuth("jdbc:dag:localhost", "testUser", "testPassword");
    }

    @Test
    void testSecure_AddsAuthorizationHeader() throws Exception {
        // Arrange: Create a mock connection
        mockConnection = mock(HttpURLConnection.class);

        // Act: Call the secure method to set the authorization header
        dagJDBCAuth.secure(mockConnection);

        // Capture the Authorization header value
        var encodedAuthHeaderCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockConnection).setRequestProperty(eq("Authorization"), encodedAuthHeaderCaptor.capture());

        // Assert: Check that the authorization header contains the expected base64-encoded string
        String encodedAuthorization = encodedAuthHeaderCaptor.getValue();
        assertTrue(encodedAuthorization.startsWith("Bearer "));
        
        // Decode and verify the contents of the authorization JSON
        String decodedAuthorization = new String(Base64.getDecoder().decode(encodedAuthorization.substring(7)), StandardCharsets.UTF_8);
        JSONObject authJson = new JSONObject(decodedAuthorization);
        assertEquals("testUser", authJson.getString("username"));
        assertEquals("included", authJson.getString("mode"));
        assertTrue(authJson.has("private_key"));
        assertTrue(authJson.has("blind_signature"));
        assertTrue(authJson.has("challenge"));
    }

    
    @Test
    void testGettersAndSetters() {
        // Act & Assert: Test the getters and setters for username, password, and nurl
        dagJDBCAuth.setUsername("newUser");
        assertEquals("newUser", dagJDBCAuth.getUsername());

        dagJDBCAuth.setPassword("newPass");
        assertEquals("newPass", dagJDBCAuth.getPassword());

        dagJDBCAuth.setNurl("newUrl");
        assertEquals("newUrl", dagJDBCAuth.getNurl());
    }
}
