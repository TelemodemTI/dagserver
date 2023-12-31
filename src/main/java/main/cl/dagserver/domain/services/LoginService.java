package main.cl.dagserver.domain.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Service;
import main.cl.dagserver.domain.core.TokenEngine;
import main.cl.dagserver.domain.model.UserDTO;

import main.cl.dagserver.application.ports.input.LoginUseCase;
import main.cl.dagserver.application.ports.output.SchedulerRepositoryOutputPort;


@Service
@ImportResource("classpath:properties-config.xml")
public class LoginService implements LoginUseCase ,UnaryOperator<String> {

	private TokenEngine tokenEngine;
	private SchedulerRepositoryOutputPort repository;
	
	@Value( "${param.jwt_secret}" )
	private String jwtSecret;
	
	@Value( "${param.jwt_signer}" )
	private String jwtSigner;
	
	@Value( "${param.jwt_subject}" )
	private String jwtSubject;
	
	@Value( "${param.jwt_ttl}" )
	private Integer jwtTtl;
	
	@Autowired
	public LoginService(TokenEngine tokenEngine,SchedulerRepositoryOutputPort repository) {
		this.tokenEngine = tokenEngine;
		this.repository = repository;
	}
	
	private String login(JSONObject reqobject) throws NoSuchAlgorithmException {
		String username = reqobject.getString("username");
		List<UserDTO> list = repository.findUser(username);
		if(!list.isEmpty() ) {
			UserDTO user = list.get(0);
			String pkstr = user.getPwdhash() + reqobject.getString("private_key");
            String hashHex = calculateHash(pkstr);
            String hashHexCh = calculateHash(reqobject.getString("challenge"));
            String combinedData = hashHex + hashHexCh;
            String hashHexCombined = calculateHash(combinedData);
            if(reqobject.getString("blind_signature").equals(hashHexCombined)){
            	Map<String,String> claims = new HashMap<>();
				claims.put("typeAccount", user.getTypeAccount());
				claims.put("username", username);
				claims.put("userid", user.getId().toString());
				return tokenEngine.tokenize(jwtSecret, jwtSigner, jwtSubject, jwtTtl, claims);
            } else {
            	return "";
            }
		} else {
			return "";
		}
	}	
	@Override
	public String apply(String t) {
		try {
			byte[] decodedBytes = Base64.getDecoder().decode(t);
            String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(decodedString);

			return this.login(jsonObject);
		} catch (Exception e) {
			return "";
		}
	}
	
	public static String calculateHash(String input) throws NoSuchAlgorithmException {
        
            // Crear un objeto de MessageDigest para SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Obtener el hash calculado en bytes
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // Convertir el hash a una representación hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        
    }

}