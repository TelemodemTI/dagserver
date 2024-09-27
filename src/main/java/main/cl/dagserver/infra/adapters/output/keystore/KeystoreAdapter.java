package main.cl.dagserver.infra.adapters.output.keystore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import main.cl.dagserver.application.ports.output.KeystoreOutputPort;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.CredentialsDTO;
import main.cl.dagserver.domain.model.KeystoreEntryDTO;

@Repository
public class KeystoreAdapter implements KeystoreOutputPort {

	@Value("${param.keystore.password}")
	private String password;
	
	@Autowired
	private KeyStore local;
	
	@Override
	public List<KeystoreEntryDTO> getEntries() throws DomainException {
	    List<KeystoreEntryDTO> entries = new ArrayList<>();
	    try {
	        var aliases = local.aliases();
	        while (aliases.hasMoreElements()) {
	            String alias = aliases.nextElement();
	            String type;
	            if (local.isCertificateEntry(alias)) {
	                type = "Certificate";
	            } else if (local.isKeyEntry(alias)) {
	                type = "Key";
	            } else {
	                type = "Unknown";
	            }
	            KeystoreEntryDTO entry = new KeystoreEntryDTO();
	            entry.setName(alias);
	            entry.setType(type);
	            entries.add(entry);
	        }
	        return entries;
	    } catch (Exception e) {
	        throw new DomainException(e);
	    }
	}

	@Override
	public void createKey(String alias, CredentialsDTO data) throws DomainException {
	    try {
	    	JSONObject json = new JSONObject();
	        json.put("username", data.getUsername());
	        json.put("password", data.getPassword());
	        byte[] keyBytes = json.toString().getBytes();
	        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
	        KeyStore.SecretKeyEntry secret = new KeyStore.SecretKeyEntry(secretKey);
	        KeyStore.ProtectionParameter password1 = new KeyStore.PasswordProtection(this.password.toCharArray());
	        local.setEntry(alias, secret, password1);
	    } catch (Exception e) {
	        throw new DomainException(e);
	    }
	}


	@Override
	public void removeKey(String alias) throws DomainException {
	    try {
	        if (local.containsAlias(alias)) {
	            local.deleteEntry(alias);
	        } else {
	            throw new DomainException(new Exception("Alias not found in the KeyStore: " + alias));
	        }
	    } catch (Exception e) {
	        throw new DomainException(e);
	    }
	}

	@Override
	public CredentialsDTO getCredentials(String alias) throws DomainException {
	    try {
	        KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) local.getEntry(alias, new KeyStore.PasswordProtection(this.password.toCharArray()));
	        SecretKey secretKey = secretKeyEntry.getSecretKey();

	        // Obtener los bytes de la clave (que representan el JSON)
	        byte[] keyBytes = secretKey.getEncoded();

	        // Convertir los bytes a String JSON
	        String jsonStr = new String(keyBytes);

	        // Convertir el JSON a CredentialsDTO
	        JSONObject json = new JSONObject(jsonStr);
	        CredentialsDTO credentials = new CredentialsDTO();
	        credentials.setUsername(json.getString("username"));
	        credentials.setPassword(json.getString("password"));

	        return credentials;
	    } catch (Exception e) {
	        throw new DomainException(e);
	    }
	}


	@Override
	public void importKeystore(Path tempFile, String originalFilename) throws DomainException {
	    try (FileInputStream fis = new FileInputStream(tempFile.toFile())) {
	        KeyStore importedKeystore = KeyStore.getInstance(KeyStore.getDefaultType());
	        char[] password1 = this.password.toCharArray(); 
	        importedKeystore.load(fis, password1);
	        KeyStore.ProtectionParameter passwordProtected = new KeyStore.PasswordProtection(this.password.toCharArray());
	        Enumeration<String> aliases = importedKeystore.aliases();
	        while (aliases.hasMoreElements()) {
	            String alias = aliases.nextElement();
	            KeyStore.Entry entry = importedKeystore.getEntry(alias,passwordProtected);
	            local.setEntry(alias, entry, new KeyStore.PasswordProtection(password1));
	        }
	    } catch (Exception e) {
	        throw new DomainException(e);
	    }
	}

	public File generateKeystoreFile(String filename) throws DomainException {
	    try {
	        //el archivo que luego necesito importar a travez del metodo importKeystore
	    	//se crea en este metodo
	    	File jksFile = File.createTempFile(filename, ".jks");
	        try (FileOutputStream fos = new FileOutputStream(jksFile)) {
	            local.store(fos, this.password.toCharArray());
	        }
	        return jksFile;
	    } catch (Exception e) {
	        throw new DomainException(e);
	    }
	}

	
}
