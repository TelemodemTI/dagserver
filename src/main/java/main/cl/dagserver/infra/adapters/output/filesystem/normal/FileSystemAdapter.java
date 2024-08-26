package main.cl.dagserver.infra.adapters.output.filesystem.normal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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
	@Override
	public void upload(Path tempFile, String uploadPath,String realname) throws DomainException {
	    try {
	        Path destinationPath = this.getFolderPath(uploadPath+realname);
	        if (destinationPath.getParent() != null) {
	            Files.createDirectories(destinationPath.getParent());
	        }
	        Files.copy(tempFile, destinationPath, StandardCopyOption.REPLACE_EXISTING);
	    } catch (IOException e) {
	        throw new DomainException(e);
	    }
	}

	@Override
	public void createFolder(String foldername) throws DomainException {
	    try {
	        Path folderPath = this.getFolderPath(foldername);
	        Files.createDirectories(folderPath);
	    } catch (IOException e) {
	        throw new DomainException(e);
	    }
	}
	
	@Override
	public void delete(String folder, String file) throws DomainException {
	    try {
	        Path targetPath;
	        if (file == null || file.isEmpty()) {
	            targetPath = this.getFolderPath(folder);
	        } else {
	            String realpath = (folder + "/" + file).replace("//", "/");
	            targetPath = this.getFolderPath(realpath);
	        }
	        Files.delete(targetPath);
	    } catch (IOException e) {
	        throw new DomainException(e);
	    }
	}
	@Override
	public Path getFilePath(String folderPath, String filename) {
		String pathfolder = folderPath + "/" + filename;
		return Paths.get(pathfolder.replace("//", ""));
	}
}
