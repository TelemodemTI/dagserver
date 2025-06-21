package main.cl.dagserver.unitest.infra.adapters.output.compiler;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.cl.dagserver.infra.adapters.output.compiler.DirectoryClassFileLocator;

class DirectoryClassFileLocatorTest {
	 
	DirectoryClassFileLocator locator;
	
	@BeforeEach
    void init() {
		locator = new DirectoryClassFileLocator("c:\\tmp\\dagrags\\");
	}
	
	@Test
	void locate() throws IOException {
		var rv = locator.locate("Test");
		assertNotNull(rv);
	}
}