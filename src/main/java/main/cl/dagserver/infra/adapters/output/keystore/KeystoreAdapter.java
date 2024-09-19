package main.cl.dagserver.infra.adapters.output.keystore;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

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


}
