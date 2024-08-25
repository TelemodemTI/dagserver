package main.cl.dagserver.infra.adapters.output.filesystem.memory;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import main.cl.dagserver.application.ports.output.FileSystemOutputPort;
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
		return this.fs.getPath(jarname);
	}

	@Override
	public Path getJDBCDriversPath(String inputPath) {
		String realpath = ("/work/"+inputPath).replace("//", "/");
		return this.fs.getPath(realpath);
	}

}
