package main.cl.dagserver.infra.adapters.output.filesystem.memory;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.DirectoryEntryDTO;
import main.cl.dagserver.infra.adapters.output.filesystem.DagFileSystem;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@Profile("filesystem-memory")
public class JimfsAdapter extends DagFileSystem {

	private static final String WORK = "/work/";
	private static final String SEP = "/";
	private static final String DSEP = "//";
	private FileSystem fs;
	
	public JimfsAdapter(){
		this.fs = Jimfs.newFileSystem(Configuration.unix());
	}
	
	@Override
	public Path getFolderPath() {
		return this.fs.getPath(WORK);
	}

	@Override
	public Path getFolderPath(String jarname) {
		return this.fs.getPath(WORK + jarname);
	}

	@Override
	public Path getJDBCDriversPath(String inputPath) {
		String realpath = (WORK+inputPath).replace(DSEP, SEP);
		return this.fs.getPath(realpath);
	}
	@Override
	public DirectoryEntryDTO getContents() throws DomainException {
		DirectoryEntryDTO directoryEntry = new DirectoryEntryDTO();
	    directoryEntry.setPath(SEP);	    
	    directoryEntry.setContent(getFileEntries(this.getFolderPath()));
	    return directoryEntry;
	}

	@Override
	public void upload(Path tempFile, String uploadPath,String realname) throws DomainException {
	    try {
	    	String realnamec = (uploadPath+SEP+realname).replace(DSEP, SEP);
	        Path destinationPath = this.getFolderPath(realnamec);
	        this.createIfNull(destinationPath);
	        Files.copy(tempFile, destinationPath);
	    } catch (IOException e) {
	        throw new DomainException(e);
	    }
	}
	private void createIfNull(Path destinationPath) {
		try {
        	if (destinationPath.getParent() != null) {
	            Files.createDirectories(destinationPath.getParent().normalize());
	        }
		} catch (Exception e) {
			log.error(e);
		}
	}
	

	@Override
	public Path getFilePath(String folderPath, String filename) {
		String rurl = (WORK + folderPath + SEP + filename).replace(DSEP, SEP);
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
	        if(filename.equals(copyname)) {
	        	throw new DomainException(new Exception("file is the same"));
	        }
	        Files.copy(sourcePath, destinationPath);
	    } catch (Exception e) {
	        throw new DomainException(e);
	    }
	}


	@Override
	public void moveFile(String folder,String filename, String newpath) throws DomainException {
	    try {
	        Path sourcePath = this.getFilePath("", filename);
	        Path destinationPath = this.getFilePath("", (newpath+SEP+filename).replace(DSEP, SEP));
	        if (destinationPath.getParent() != null) {
	            Files.createDirectories(destinationPath.getParent());
	        }
	        if(folder.equals(newpath)) {
	        	throw new DomainException(new Exception("file is the same"));
	        }
	        Files.move(sourcePath, destinationPath);
	    } catch (IOException e) {
	        throw new DomainException(e);
	    }
	}

}
