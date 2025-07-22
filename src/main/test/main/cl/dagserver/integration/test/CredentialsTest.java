package main.cl.dagserver.integration.test;

import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;

import lombok.extern.log4j.Log4j2;
import main.cl.dagserver.integration.pom.AuthenticatedPage;
import main.cl.dagserver.integration.pom.CredentialsPage;
import main.cl.dagserver.integration.pom.LoginPage;
import main.cl.dagserver.integration.test.core.BaseIntegrationTest;

@Log4j2
public class CredentialsTest extends BaseIntegrationTest {
    
    @Test(priority = 1)
    public void getCredentials() {
        log.info("getCredentials");
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
            AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            CredentialsPage credentialsPage = authenticatedPage.goToCredentials();
            var credentials = credentialsPage.getActualCredentials();
            Assertions.assertTrue(credentials.size() > 0);
            authenticatedPage.logout();
            Assertions.assertTrue(true);
        }
    }

    @Test(priority = 2)
    public void createNewCredential() {
        log.info("createNewCredential");
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
            AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            CredentialsPage credentialsPage = authenticatedPage.goToCredentials();
            if(credentialsPage.existCredential("dagserver1")){
                credentialsPage.deleteCredential("dagserver1");
                authenticatedPage.goToCredentials();
            }
            credentialsPage.createNewCredentialsModal().create("dagserver1", "dagserver1", "USER");
            authenticatedPage.goToCredentials();
            if(!credentialsPage.existCredential("dagserver1")){
                Assertions.fail("no se agrego el usuario");
            }
            authenticatedPage.logout();
            Assertions.assertTrue(true);
        }
    }

    @Test(priority = 3)
    public void deleteCredential() {
        log.info("deleteCredential");
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
            AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            CredentialsPage credentialsPage = authenticatedPage.goToCredentials();
            if(!credentialsPage.existCredential("dagserver1")){
                credentialsPage.createNewCredentialsModal().create("dagserver1", "dagserver1", "USER");	
                authenticatedPage.goToCredentials();
            }
            credentialsPage.deleteCredential("dagserver1");
            authenticatedPage.goToCredentials();
            if(credentialsPage.existCredential("dagserver1")){
                Assertions.fail("no se elimino el usuario");
            }   
            authenticatedPage.logout();
            Assertions.assertTrue(true);
        }
    }
}
