package main.cl.dagserver.infra.adapters.output.compiler;

import static org.testng.Assert.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DirectoryClassFileLocatorTest {
	
	DirectoryClassFileLocator locator;
	
	@BeforeEach
    void init() {
		locator = new DirectoryClassFileLocator("c:\\tmp\\");
	}
	
	@Test
	void locate() throws IOException {
		var rv = locator.locate("Test");
		assertNotNull(rv);
	}
}
