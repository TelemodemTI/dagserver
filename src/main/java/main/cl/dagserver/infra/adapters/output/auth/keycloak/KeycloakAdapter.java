package main.cl.dagserver.infra.adapters.output.auth.keycloak;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import lombok.extern.log4j.Log4j2;
import main.cl.dagserver.application.ports.output.AuthenticationOutputPort;
import main.cl.dagserver.domain.enums.AccountType;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.AuthDTO;
import main.cl.dagserver.domain.model.SessionDTO;


@Component
@Log4j2
@ImportResource("classpath:properties-config.xml")
@Profile("auth-keycloak")
public class KeycloakAdapter implements AuthenticationOutputPort {
	
	private static final String REALMS = "realms/";
	private static final String CLIENTIDEQ = "client_id=";
	private static final String AMPCLIENTSECRET = "&client_secret=";
	
	@Value( "${param.keycloak.host}" )
	protected String keycloakHost;

	@Value( "${param.keycloak.realm}" )
	protected String keycloakRealm;
	
	@Value( "${param.keycloak.client_id}" )
	protected String clientId;
	
	@Value( "${param.keycloak.client_secret}" )
	protected String clientSecret;
	
	
	private JSONObject makeJSONPost(String urlStr,String body) throws DomainException {
		try {
			URL url = new URL(urlStr);
	        HttpURLConnection con = (HttpURLConnection) url.openConnection();
	        con.setRequestMethod("POST");
	        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	        con.setDoOutput(true);
	        OutputStream os = con.getOutputStream();
	        os.write(body.getBytes(StandardCharsets.UTF_8));
	        os.flush();
	        os.close();
	        JSONObject responsejson = null;
	        int responseCode = con.getResponseCode();
	        if (responseCode == HttpURLConnection.HTTP_OK) {
	            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	            String inputLine;
	            StringBuilder response = new StringBuilder();
	            while ((inputLine = in.readLine()) != null) {
	                response.append(inputLine);
	            }
	            in.close();
	            responsejson = new JSONObject(response.toString());
	            return responsejson;
	        } else {
	        	BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
	            String inputLine;
	            StringBuilder response = new StringBuilder();
	            while ((inputLine = in.readLine()) != null) {
	                response.append(inputLine);
	            }
	            in.close();
	            throw new DomainException(new Exception("Error in response: " + response.toString()));
	        }	
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}
	
	@Override
	public SessionDTO login(JSONObject reqobject) throws DomainException {
	    try {
	        String username = reqobject.getString("username");
	        String pwd = reqobject.getString("challenge");
	        String urlStr = keycloakHost + REALMS + keycloakRealm + "/protocol/openid-connect/token";
	        String body = CLIENTIDEQ + URLEncoder.encode(clientId, StandardCharsets.UTF_8) +
	                      AMPCLIENTSECRET + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8) +
	                      "&grant_type=" + URLEncoder.encode("password", StandardCharsets.UTF_8) +
	                      "&username=" + URLEncoder.encode(username, StandardCharsets.UTF_8) +
	                      "&password=" + URLEncoder.encode(pwd, StandardCharsets.UTF_8);
	        JSONObject responsejson = makeJSONPost(urlStr, body);
	        String token = responsejson.getString("access_token");
	        String refreshToken = responsejson.getString("refresh_token");
	        SessionDTO dto = new SessionDTO();
	        dto.setRefreshToken(refreshToken);
	        dto.setToken(token);
	        return dto;
	    } catch (Exception e) {
	    	log.error(e);
	        throw new DomainException(e);
	    }
	}
	
	@Override
	public AuthDTO untokenize(String token) throws DomainException {
		try {
			String urlStr = keycloakHost + REALMS + keycloakRealm + "/protocol/openid-connect/token/introspect";
	        String body = CLIENTIDEQ + URLEncoder.encode(clientId, StandardCharsets.UTF_8) +
	                      AMPCLIENTSECRET + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8) +
	                      "&token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
	            JSONObject responsejson = this.makeJSONPost(urlStr, body);
	            AuthDTO auth = new AuthDTO();
	            auth.setAccountType(AccountType.USER);
	            auth.setExpires(new Date(responsejson.getLong("exp")));
	    		auth.setIssueAt(new Date(responsejson.getLong("iat")));
	    		auth.setSubject("access_token");
	    		return auth;
		} catch (Exception e) {
			log.error(e);
			throw new DomainException(e);
		}
	}

	@Override
	public void logout(String token) throws DomainException {
		try {
			String urlStr = keycloakHost + REALMS + keycloakRealm + "/protocol/openid-connect/logout";
	        String body = CLIENTIDEQ + URLEncoder.encode(clientId, StandardCharsets.UTF_8) +
	                      AMPCLIENTSECRET + URLEncoder.encode(clientSecret,StandardCharsets.UTF_8) +
	                      "&refresh_token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
	        URL url = new URL(urlStr);
	        HttpURLConnection con = (HttpURLConnection) url.openConnection();
	        con.setRequestMethod("POST");
	        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	        con.setDoOutput(true);
	        OutputStream os = con.getOutputStream();
	        os.write(body.getBytes(StandardCharsets.UTF_8));
	        os.flush();
	        os.close();
	        int responseCode = con.getResponseCode();
	        if (responseCode != HttpURLConnection.HTTP_NO_CONTENT) {
	        	throw new DomainException(new Exception("error in keycloak logout"));
	        }
		} catch (Exception e) {
			log.error(e);
			throw new DomainException(e);
		}
		
	}

}
