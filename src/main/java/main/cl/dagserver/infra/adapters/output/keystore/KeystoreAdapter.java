package main.cl.dagserver.infra.adapters.output.keystore;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import main.cl.dagserver.application.ports.output.KeystoreOutputPort;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.KeystoreEntryDTO;

@Repository
public class KeystoreAdapter implements KeystoreOutputPort {

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
	public void createKey(String alias, String key, String pwd) throws DomainException {
		try {
			byte[] keyBytes = key.getBytes(); 
	        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
			KeyStore.SecretKeyEntry secret = new KeyStore.SecretKeyEntry(secretKey);
			KeyStore.ProtectionParameter password = new KeyStore.PasswordProtection(pwd.toCharArray());
			local.setEntry(alias, secret, password);	
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

	

}
