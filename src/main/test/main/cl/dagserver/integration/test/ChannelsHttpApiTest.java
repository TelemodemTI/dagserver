package main.cl.dagserver.integration.test;

import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;
import main.cl.dagserver.integration.pom.AuthenticatedPage;
import main.cl.dagserver.integration.pom.LoginPage;
import main.cl.dagserver.integration.test.core.BaseIntegrationTest;

public class ChannelsHttpApiTest extends BaseIntegrationTest{
    
    @Test(priority = 1)
    public void testCreateApiKey() throws InterruptedException {
        // Arrange
        String apiKeyName = "testApiKey";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
            AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            var channelsPage = authenticatedPage.goToChannels();
            var modal = channelsPage.openChannelHttpModal();
            modal.createApiKey(apiKeyName);

            modal = channelsPage.openChannelHttpModal();
            var actualApiKeys = modal.getActualApiKeys();
            Assertions.assertTrue(actualApiKeys.stream().anyMatch(apiKey -> apiKey.get("Application Name").equals(apiKeyName)));

            modal.deleteApiKey(apiKeyName);
            modal = channelsPage.openChannelHttpModal();
            actualApiKeys = modal.getActualApiKeys();
            Assertions.assertTrue(actualApiKeys.stream().noneMatch(apiKey -> apiKey.get("Application Name").equals(apiKeyName)));
        }
    }
}
