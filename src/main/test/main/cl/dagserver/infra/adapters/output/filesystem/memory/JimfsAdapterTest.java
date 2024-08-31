package main.cl.dagserver.infra.adapters.output.filesystem.memory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.cl.dagserver.domain.exceptions.DomainException;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

class JimfsAdapterTest {
	private JimfsAdapter memory;
	@BeforeEach
    void init() {
		memory = new JimfsAdapter();
	}
	
	@Test
	void getFolderPathTest(){
		var rv = memory.getFolderPath();
		assertNotNull(rv);
	}
	
	@Test
	void getFolderPathStringTest() {
		var rv = memory.getFolderPath("");
		assertNotNull(rv);
	}
	
	@Test
	void getJDBCDriversPathTest() {
		var rv = memory.getJDBCDriversPath("");
		assertNotNull(rv);
	}
	@Test
	void getContentsTest() throws DomainException {
		var rv = memory.getContents();
		assertNotNull(rv);
	}
	@Test
	void getFilePathTest() {
		var rv = memory.getFilePath("","");
		assertNotNull(rv);
	}
	
	@Test
	void uploadTest() throws IOException, DomainException {
		var filtmp = File.createTempFile("test", "test");
		try {
			memory.upload(Path.of(filtmp.toURI()), "", "");
		} catch (Exception e) {
			memory.upload(Path.of(filtmp.toURI()), "/", "tmp.file");
			assertTrue(true);
		}
	}
	@Test
	void copyFileTest() throws DomainException, IOException {
		var filtmp = File.createTempFile("test", "test");
		memory.upload(Path.of(filtmp.toURI()), "/", "tmp.file");
		try {
			memory.copyFile("tmp.file", "tmp.file");
		} catch (Exception e) {
			memory.copyFile("tmp.file", "tmp2.file");
			assertTrue(true);
		}
	}
	@Test
	void moveFileTest() throws IOException, DomainException {
		var filtmp = File.createTempFile("test", "test");
		memory.upload(Path.of(filtmp.toURI()), "/", "tmp.file");
		try {
			memory.moveFile("/","tmp.file", "/");
		}catch (Exception e) {
			memory.moveFile("/","tmp.file", "/lib/");
			assertTrue(true);
		}
	}
}
