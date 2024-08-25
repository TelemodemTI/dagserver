package main.cl.dagserver.infra.adapters.output.filesystem.normal;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import main.cl.dagserver.application.ports.output.FileSystemOutputPort;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.DirectoryEntryDTO;
import main.cl.dagserver.infra.adapters.output.filesystem.DagFileSystem;

@Component
@Profile("filesystem-normal")
public class FileSystemAdapter extends DagFileSystem implements FileSystemOutputPort {

	@Value("${param.folderpath}")
	private String pathfolder;
		
	@Override
	public Path getFolderPath() {
		return Paths.get(pathfolder);
		
	}
	@Override
	public Path getFolderPath(String jarname) {
		return Paths.get(pathfolder, jarname);
	}
	@Override
	public Path getJDBCDriversPath(String inputPath) {
		return Paths.get(pathfolder);
	}
	@Override
	public DirectoryEntryDTO getContents() throws DomainException {
		Path root = this.getFolderPath();
		DirectoryEntryDTO directoryEntry = new DirectoryEntryDTO();
	    directoryEntry.setPath("/");
	    directoryEntry.setContent(getFileEntries(root));
	    return directoryEntry;
	}
	
}
