package main.cl.dagserver.integration.test;

import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;
import main.cl.dagserver.integration.pom.AuthenticatedPage;
import main.cl.dagserver.integration.pom.LoginPage;
import main.cl.dagserver.integration.pom.PropertiesPage;
import main.cl.dagserver.integration.test.core.BaseIntegrationTest;

import lombok.extern.log4j.Log4j2;
@Log4j2
public class PropertiesTest extends BaseIntegrationTest {
    
  @Test(priority = 1)
  public void getActualProps() {
    log.info("getActualProps");
    LoginPage loginPage = new LoginPage(this.driver);
    if(loginPage.login("dagserver", "dagserver")){
      AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
      PropertiesPage propertiesPage = authenticatedPage.goToProperties();
      var props = propertiesPage.getActualProps();
      Assertions.assertTrue(props.size() > 0);
      authenticatedPage.logout();
      Assertions.assertTrue(true);
    }
  }
  
  
  @Test(priority = 2)
  public void createNewProperty() throws InterruptedException {
    log.info("createNewProperty");
    LoginPage loginPage = new LoginPage(this.driver);
    if(loginPage.login("dagserver", "dagserver")){
      AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
      PropertiesPage propertiesPage = authenticatedPage.goToProperties();
      propertiesPage.createNewPropertyModal().create("testing_selenium","descr","testing_selenium","testing_selenium");
      propertiesPage.search("testing_selenium");
      Assertions.assertTrue(propertiesPage.existProp("testing_selenium"));
      authenticatedPage.logout();
      Assertions.assertTrue(true);
    }
  }
  
  @Test(priority = 3)
  public void deleteProperty() throws InterruptedException {
    log.info("deleteProperty");
    LoginPage loginPage = new LoginPage(this.driver);
    if(loginPage.login("dagserver", "dagserver")){
      AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
      PropertiesPage propertiesPage = authenticatedPage.goToProperties();
      propertiesPage.deleteProp("testing_selenium");
      propertiesPage.search("testing_selenium");
      Assertions.assertFalse(propertiesPage.existProp("testing_selenium"));
      authenticatedPage.logout();
      Assertions.assertTrue(true);
    }
  }
  
  @Test(priority = 4)
  public void importProperties() throws InterruptedException {
    log.info("importProperties");
    LoginPage loginPage = new LoginPage(this.driver);
    if(loginPage.login("dagserver", "dagserver")){
      AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
      PropertiesPage propertiesPage = authenticatedPage.goToProperties();
      propertiesPage.importPropertiesModal().importProperties("prop_file.json");
      propertiesPage.search("testing_selenium");
      if(!propertiesPage.existProp("testing_selenium")) {
        Assertions.fail("no se importo la propertie");
      } else {
        authenticatedPage.logout();
        Assertions.assertTrue(true);
      }
    }
  }
  
  @Test(priority = 5)
  public void exportProperties() throws InterruptedException {
    log.info("exportProperties");
    LoginPage loginPage = new LoginPage(this.driver);
    if(loginPage.login("dagserver", "dagserver")){
      AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
      PropertiesPage propertiesPage = authenticatedPage.goToProperties();
      propertiesPage.search("testing_selenium");
      propertiesPage.exportSelectedProperties();
      Assertions.assertTrue(true);
      authenticatedPage.logout();
      Assertions.assertTrue(true);
    }
  }
  
  @Test(priority = 6)
  public void deleteByGroup() throws InterruptedException {
    log.info("deleteByGroup");
    LoginPage loginPage = new LoginPage(this.driver);
    if(loginPage.login("dagserver", "dagserver")){
      AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
      PropertiesPage propertiesPage = authenticatedPage.goToProperties();
      propertiesPage.deleteByGroup("testing_selenium");
      propertiesPage.search("testing_selenium");
      Assertions.assertFalse(propertiesPage.existProp("testing_selenium"));
      authenticatedPage.logout();
      Assertions.assertTrue(true);
    }
  }
  
}
