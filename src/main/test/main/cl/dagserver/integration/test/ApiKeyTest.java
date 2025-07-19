package main.cl.dagserver.integration.test;

import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;

import main.cl.dagserver.integration.pom.ApiKeyPage;
import main.cl.dagserver.integration.pom.AuthenticatedPage;
import main.cl.dagserver.integration.pom.LoginPage;
import main.cl.dagserver.integration.test.core.BaseIntegrationTest;

public class ApiKeyTest extends BaseIntegrationTest{

	@Test(priority = 1)
	public void testApiKey() throws InterruptedException {
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

