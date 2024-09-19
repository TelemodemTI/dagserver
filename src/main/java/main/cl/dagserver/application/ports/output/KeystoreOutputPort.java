package main.cl.dagserver.application.ports.output;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.KeystoreEntryDTO;

public interface KeystoreOutputPort {
	public List<KeystoreEntryDTO> getEntries() throws DomainException;
	public void createKey(String alias, String key, String pwd) throws DomainException;
	public void removeKey(String alias) throws DomainException;
	public File generateKeystoreFile(String filename, String password) throws DomainException;
	public void importKeystore(Path tempFile, String originalFilename) throws DomainException;
}
