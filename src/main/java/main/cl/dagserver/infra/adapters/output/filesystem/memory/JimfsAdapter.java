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
		String rurl = ("/work/" + folderPath + "/" + filename).replace("//", "/");
		return this.fs.getPath(rurl);
	}



}
