package main.cl.dagserver.domain.core;

import com.auth0.jwt.JWT;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import main.cl.dagserver.domain.exceptions.DomainException;

import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.JWTVerifier;

@Component
public class TokenEngine {
	

	public String tokenize(String secret ,String issuer,String subject,Integer milisec,Map<String,String> claims ){
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

	
	public Map<String,Object> untokenize(String token,String secret,String issuer){
		Map<String,Object> result = new HashMap<>();
		Map<String,String> claims = new HashMap<>();

		Algorithm algorithm = Algorithm.HMAC256(secret);
		JWTVerifier verifier = JWT.require(algorithm).withIssuer(issuer).build();
		DecodedJWT jwt = verifier.verify(token);
		Date expires = jwt.getExpiresAt();
		Date issuedAt = jwt.getIssuedAt();
		for (Map.Entry<String, Claim> entry : jwt.getClaims().entrySet()) {
			claims.put(entry.getKey(), entry.getValue().asString());
		}
		result.put("claims", claims);
		result.put("expire", expires);
		result.put("issued", issuedAt);
		result.put("subject", jwt.getSubject());
		return result;
	}
	public String sha256(String base) throws DomainException  {
	    try {
	    	MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(base.getBytes(StandardCharsets.UTF_8));
			StringBuilder hexString = new StringBuilder();
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
			    if(hex.length() == 1) hexString.append('0');
			    hexString.append(hex);
			}
			return hexString.toString();	
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}
}
