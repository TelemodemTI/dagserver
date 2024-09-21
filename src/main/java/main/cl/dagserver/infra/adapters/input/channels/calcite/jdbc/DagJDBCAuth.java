package main.cl.dagserver.infra.adapters.input.channels.calcite.jdbc;

import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.crypto.digests.SHA256Digest;
import org.json.JSONObject;

public class DagJDBCAuth {

	private String username;
	private String password;
	private String nurl;
	public DagJDBCAuth(String url, String username, String password){
		this.username = username;
		this.password = password;
		this.nurl = url.replace("jdbc:dag:", "http://") + "/calcite/execute";
	}
	
	public void secure(HttpURLConnection connection) {
		var desafio = generateRandomKey(16);
		Map<String,String> blindFirm = generateBlind(password, desafio);
		blindFirm.put("challenge", desafio);
		blindFirm.put("username", username);
		blindFirm.put("mode", "included");
		JSONObject json = new JSONObject(blindFirm);
		String encodedAuthorization = Base64.getEncoder().encodeToString(json.toString().getBytes(StandardCharsets.UTF_8));
		connection.setRequestProperty("Authorization", "Bearer " + encodedAuthorization);
	}
	
	private Map<String,String> generateBlind(String password, String desafio){
		Map<String,String> map = new HashMap<>();
        String passwordHash = hashSHA256(password);
        String privateKey = generateRandomKey(16);
        String publicKey = hashSHA256(passwordHash + privateKey);
        String challengeHash = hashSHA256(desafio);
        String blindSignature = hashSHA256(publicKey + challengeHash);
        map.put("private_key", privateKey);
        map.put("blind_signature", blindSignature);
        return map;
	}
	
	private String generateRandomKey(int length) {
	    SecureRandom secureRandom = new SecureRandom();
	    byte[] key = new byte[length];
	    secureRandom.nextBytes(key);
	    return bytesToHex(key);
	}
	private String hashSHA256(String input) {
	    SHA256Digest digest = new SHA256Digest();
	    byte[] hash = new byte[digest.getDigestSize()];
	    byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
	    digest.update(inputBytes, 0, inputBytes.length);
	    digest.doFinal(hash, 0);
	    return bytesToHex(hash);
	}
	private String bytesToHex(byte[] bytes) {
	    StringBuilder hexString = new StringBuilder(2 * bytes.length);
	    for (byte b : bytes) {
	        String hex = Integer.toHexString(0xff & b);
	        if (hex.length() == 1) {
	            hexString.append('0');
	        }
	        hexString.append(hex);
	    }
	    return hexString.toString();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNurl() {
		return nurl;
	}

	public void setNurl(String nurl) {
		this.nurl = nurl;
	}
}
