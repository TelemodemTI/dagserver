package main.cl.dagserver.infra.adapters.output.auth.internal;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.extern.log4j.Log4j2;
import main.cl.dagserver.application.ports.output.AuthenticationOutputPort;
import main.cl.dagserver.application.ports.output.SchedulerRepositoryOutputPort;
import main.cl.dagserver.domain.enums.AccountType;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.AuthDTO;
import main.cl.dagserver.domain.model.UserDTO;

@Component
@Log4j2
@ImportResource("classpath:properties-config.xml")
@Profile("auth-internal")
public class AuthenticationAdapter implements AuthenticationOutputPort {

	@Value( "${param.jwt_secret}" )
	protected String jwtSecret;
	@Value( "${param.jwt_signer}" )
	protected String jwtSigner;
	
	@Value( "${param.jwt_subject}" )
	protected String jwtSubject;
	
	@Value( "${param.jwt_ttl}" )
	protected Integer jwtTtl;
	
	@Autowired
	protected SchedulerRepositoryOutputPort repository;
	
	private String tokenize(String secret, String issuer, String subject, Integer milisec, Map<String, String> claims) {
		Algorithm algorithm = Algorithm.HMAC256(secret);
		Builder builder = JWT.create().withIssuer(issuer); 
		builder.withIssuedAt(new Date());
		builder.withExpiresAt(new Date(System.currentTimeMillis() + milisec));
		for (Map.Entry<String, String> entry : claims.entrySet()) {
			builder.withClaim(entry.getKey(), entry.getValue());
		}
		builder.withSubject(subject);
		return builder.sign(algorithm);
	}
	@Override
	public AuthDTO untokenize(String token) {
		AuthDTO result = new AuthDTO();
		Map<String,String> claims = new HashMap<>();

		Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
		JWTVerifier verifier = JWT.require(algorithm).withIssuer(jwtSigner).build();
		DecodedJWT jwt = verifier.verify(token);
		Date expires = jwt.getExpiresAt();
		Date issuedAt = jwt.getIssuedAt();
		for (Map.Entry<String, Claim> entry : jwt.getClaims().entrySet()) {
			claims.put(entry.getKey(), entry.getValue().asString());
		}
		result.setExpires(expires);
		result.setIssueAt(issuedAt);
		result.setSubject(jwt.getSubject());
		result.setAccountType(AccountType.valueOf(claims.get("typeAccount")));
		return result;
	}
	@Override
	public String login(JSONObject reqobject) throws DomainException {
		try {
			String username = reqobject.getString("username");
			List<UserDTO> list = repository.findUser(username);
			if(!list.isEmpty() ) {
				UserDTO user = list.get(0);
				String pkstr = user.getPwdhash() + reqobject.getString("private_key");
	            String hashHex = this.calculateHash(pkstr);
	            String hashHexCh = this.calculateHash(reqobject.getString("challenge"));
	            String combinedData = hashHex + hashHexCh;
	            String hashHexCombined = calculateHash(combinedData);
	            if(reqobject.getString("blind_signature").equals(hashHexCombined)){
	            	Map<String,String> claims = new HashMap<>();
					claims.put("typeAccount", user.getTypeAccount().toString());
					claims.put("username", username);
					claims.put("userid", user.getId().toString());
					return this.tokenize(jwtSecret, jwtSigner, jwtSubject, jwtTtl, claims);
	            } else {
	            	return "";
	            }
			} else {
				return "";
			}	
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}
	private String calculateHash(String input) throws NoSuchAlgorithmException {
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
	@Override
	public void logout(String token) {
		log.debug(token);
	}
}
