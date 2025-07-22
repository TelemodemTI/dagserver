package main.cl.dagserver.integration.test;

import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;

import lombok.extern.log4j.Log4j2;
import main.cl.dagserver.integration.pom.ApiKeyPage;
import main.cl.dagserver.integration.pom.AuthenticatedPage;
import main.cl.dagserver.integration.pom.LoginPage;
import main.cl.dagserver.integration.test.core.BaseIntegrationTest;

@Log4j2
public class ApiKeyTest extends BaseIntegrationTest{

	@Test(priority = 1)
	public void testApiKey() throws InterruptedException {
        log.info("testApiKey");
		String apiKey = "test";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
            AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            ApiKeyPage apiKeyPage = authenticatedPage.goToApiKey();
            apiKeyPage.createApiKey(apiKey);
            Assertions.assertTrue(apiKeyPage.existApikey(apiKey));
            apiKeyPage.deleteApiKey(apiKey);
            Assertions.assertFalse(apiKeyPage.existApikey(apiKey));
        }      
    }

}

