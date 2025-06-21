package main.cl.dagserver.integration.test.core;

import lombok.extern.log4j.Log4j2;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

@Log4j2
public class BaseIntegrationTest {  
  private GenericContainer<?> application;
  private GenericContainer<?> seleniumContainer;
  protected WebDriver driver;
  private String host;
  private Integer port;
  private String appUrl;
	
  
  @SuppressWarnings("resource")
  @BeforeClass
  public void beforeAll() {
	  String hostPath = Paths.get("selenium").toAbsolutePath().toString();
      String containerPath = "/selenium";
	  DockerImageName seleniumImage = DockerImageName.parse("selenium/standalone-chrome:latest");
	  this.application = new GenericContainer<>(DockerImageName.parse("maximolira/dagserver:latest"))
			  .withEnv("APP_PROFILES_DEFAULT", "auth-internal,filesystem-normal")
			  .withEnv("APP_FOLDERPATH", "/root/dags/")
        .withEnv("APP_BACKGROUND_JOBS", "0 0/1 * ? * *")
			  .withExposedPorts(8081);
      this.seleniumContainer = new GenericContainer<>(seleniumImage)
          .withExposedPorts(4444)
          .withSharedMemorySize(2L * 1024L * 1024L * 1024L)
          .withEnv("SE_VNC_NO_PASSWORD", "1")
          .withFileSystemBind(hostPath, containerPath);
      this.seleniumContainer.start();
      this.application.start();
      this.appUrl = "http://host.docker.internal:"+this.application.getFirstMappedPort().toString()+"/";
      this.host = seleniumContainer.getHost();
      this.port = seleniumContainer.getMappedPort(4444);
      URL url;
      try {
          url = new URL("http://" + this.host + ":" + this.port + "/wd/hub");
          ChromeOptions options = new ChromeOptions(); 
          driver = new RemoteWebDriver(url, options);   
          driver.manage().window().maximize();
          log.info("url: {}","http://" + this.host + ":" + this.port);
          
      } catch (MalformedURLException e) {
          e.printStackTrace();
      }
      driver.get(appUrl);
	  log.error("Docker images Ready");
  }

  @AfterClass
  public void afterAll() {
	try {
		this.driver.quit();
		this.application.stop();
	  	this.seleniumContainer.stop();  
		
	} catch (Exception e) {
		
	}
	log.error("Docker Images Shutdown");
  }

}
