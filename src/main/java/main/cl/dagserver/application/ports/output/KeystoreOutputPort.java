package main.cl.dagserver.application.ports.output;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.CredentialsDTO;
import main.cl.dagserver.domain.model.KeystoreEntryDTO;

public interface KeystoreOutputPort {
	public List<KeystoreEntryDTO> getEntries() throws DomainException;
	public void createKey(String alias, CredentialsDTO data) throws DomainException;
	public void removeKey(String alias) throws DomainException;
	public File generateKeystoreFile(String filename) throws DomainException;
	public void importKeystore(Path tempFile, String originalFilename) throws DomainException;
	public CredentialsDTO getCredentials(String alias) throws DomainException;;
}
