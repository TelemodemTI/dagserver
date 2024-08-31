package main.cl.dagserver.infra.adapters.output.filesystem.memory;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import main.cl.dagserver.application.ports.output.FileSystemOutputPort;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.DirectoryEntryDTO;
import main.cl.dagserver.infra.adapters.output.filesystem.DagFileSystem;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

@Component
@Profile("filesystem-memory")
public class JimfsAdapter extends DagFileSystem implements FileSystemOutputPort {

	private FileSystem fs;
	
	public JimfsAdapter(){
		this.fs = Jimfs.newFileSystem(Configuration.unix());
	}
	
	@Override
	public Path getFolderPath() {
		return this.fs.getPath("/work/");
	}

	@Override
	public Path getFolderPath(String jarname) {
		return this.fs.getPath("/work/" + jarname);
	}

	@Override
	public Path getJDBCDriversPath(String inputPath) {
		String realpath = ("/work/"+inputPath).replace("//", "/");
		return this.fs.getPath(realpath);
	}
	@Override
	public DirectoryEntryDTO getContents() throws DomainException {
		DirectoryEntryDTO directoryEntry = new DirectoryEntryDTO();
	    directoryEntry.setPath("/");	    
	    directoryEntry.setContent(getFileEntries(this.getFolderPath()));
	    return directoryEntry;
	}

	@Override
	public void upload(Path tempFile, String uploadPath,String realname) throws DomainException {
	    try {
	    	String realnamec = (uploadPath+"/"+realname).replace("//", "/");
	        Path destinationPath = this.getFolderPath(realnamec);
	        try {
	        	if (destinationPath.getParent() != null) {
		            Files.createDirectories(destinationPath.getParent());
		        }
			} catch (Exception e) {}
	        Files.copy(tempFile, destinationPath);
	    } catch (IOException e) {
	        throw new DomainException(e);
	    }
	}

	

	@Override
	public Path getFilePath(String folderPath, String filename) {
		String rurl = ("/work/" + folderPath + "/" + filename).replace("//", "/");
		return this.fs.getPath(rurl);
	}

	@Override
	public void copyFile(String filename, String copyname) throws DomainException {
	    try {
	        Path sourcePath = this.getFilePath("", filename);
	        Path destinationPath = this.getFilePath("", copyname);
	        if (destinationPath.getParent() != null) {
	            Files.createDirectories(destinationPath.getParent());
	        }
	        Files.copy(sourcePath, destinationPath);
	    } catch (IOException e) {
	        throw new DomainException(e);
	    }
	}


	@Override
	public void moveFile(String folder,String filename, String newpath) throws DomainException {
	    try {
	        Path sourcePath = this.getFilePath("", filename);
	        Path destinationPath = this.getFilePath("", (newpath+"/"+filename).replace("//", "/"));
	        if (destinationPath.getParent() != null) {
	            Files.createDirectories(destinationPath.getParent());
	        }
	        Files.move(sourcePath, destinationPath);
	    } catch (IOException e) {
	        throw new DomainException(e);
	    }
	}

}
