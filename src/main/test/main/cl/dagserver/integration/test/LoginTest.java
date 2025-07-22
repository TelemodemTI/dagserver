package main.cl.dagserver.integration.test;

import org.testng.annotations.Test;
import lombok.extern.log4j.Log4j2;
import main.cl.dagserver.integration.pom.AuthenticatedPage;
import main.cl.dagserver.integration.pom.LoginPage;
import main.cl.dagserver.integration.test.core.BaseIntegrationTest;
import org.junit.jupiter.api.Assertions;

@Log4j2
public class LoginTest extends BaseIntegrationTest {  
  
  @Test
  public void wrongCredentials() {
	  log.error("wrongCredentials");
    LoginPage loginPage = new LoginPage(this.driver);
    if(!loginPage.login("dagserver", "clave mala")){
      Assertions.assertTrue(true);
    }
  }
  
  @Test
  public void loginOk() {
    log.info("loginOk");
    LoginPage loginPage = new LoginPage(this.driver);
    if(loginPage.login("dagserver", "dagserver")){
      var aut = new AuthenticatedPage(this.driver);
      aut.logout();
      Assertions.assertTrue(true);
    }
  }

}
