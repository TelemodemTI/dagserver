package main.cl.dagserver.application.ports.output;

import java.util.List;

import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.KeystoreEntryDTO;

public interface KeystoreOutputPort {
	public List<KeystoreEntryDTO> getEntries() throws DomainException;
	public void createKey(String alias, String key, String pwd) throws DomainException;
	public void removeKey(String alias) throws DomainException;
}
