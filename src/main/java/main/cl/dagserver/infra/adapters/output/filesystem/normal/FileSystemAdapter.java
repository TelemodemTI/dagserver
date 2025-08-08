package main.cl.dagserver.infra.adapters.output.filesystem.normal;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.DirectoryEntryDTO;
import main.cl.dagserver.infra.adapters.output.filesystem.DagFileSystem;

@Component
@Profile("filesystem-normal")
public class FileSystemAdapter extends DagFileSystem {

	@Value("${param.folderpath}")
	private String pathfolder;
		
	@Override
	public Path getPath(String jarname) {
		return Paths.get(pathfolder, jarname);
	}
	@Override
	public DirectoryEntryDTO getContents() throws DomainException {
		Path root = this.getPath("/");
		DirectoryEntryDTO directoryEntry = new DirectoryEntryDTO();
	    directoryEntry.setName("/");
	    directoryEntry.setType("folder");
	    directoryEntry.setContent(getFileEntries(root));
	    return directoryEntry;
	}
	@Override
	public void upload(Path tempFile, String uploadPath,String realname) throws DomainException {
	    try {
	    	Path sanitizedUploadPath = Paths.get(uploadPath).normalize(); 
	        Path sanitizedRealname = Paths.get(realname).getFileName(); // Solo toma el nombre del archivo
	        Path destinationPath = this.getPath(sanitizedUploadPath.toString()).resolve(sanitizedRealname);
	        if (destinationPath.getParent() != null) {
	            Files.createDirectories(destinationPath.getParent());
	        }
	        Files.copy(tempFile.normalize(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
	    } catch (IOException e) {
	        throw new DomainException(e);
	    }
	}

	
	@Override
	public Path getFilePath(String folderPath, String filename) {
		String pathfolder1 = folderPath + File.separator + filename;
		return Paths.get(pathfolder1.replace("//", ""));
	}
	@Override
	public void copyFile(String filename, String copyname) throws DomainException {
	    try {
	        Path sourcePath = this.getFilePath("", filename);
	        Path destinationPath = this.getFilePath("", copyname);
	        if (destinationPath.getParent() != null) {
	            Files.createDirectories(destinationPath.getParent());
	        }
	        Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
	    } catch (IOException e) {
	        throw new DomainException(e);
	    }
	}

	@Override
	public void moveFile(String folder,String filename, String newpath) throws DomainException {
	    try {
	        Path sourcePath = this.getFilePath("", filename);
	        Path destinationPath = this.getFilePath("", (newpath+File.separator+filename).replace("//", File.separator));
	        if (destinationPath.getParent() != null) {
	            Files.createDirectories(destinationPath.getParent());
	        }
	        Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
	    } catch (IOException e) {
	        throw new DomainException(e);
	    }
	}
	
}
