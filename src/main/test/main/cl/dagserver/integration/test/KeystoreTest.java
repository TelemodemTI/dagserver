package main.cl.dagserver.integration.test;

import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;

import main.cl.dagserver.integration.pom.AuthenticatedPage;
import main.cl.dagserver.integration.pom.KeystorePage;
import main.cl.dagserver.integration.pom.LoginPage;
import main.cl.dagserver.integration.test.core.BaseIntegrationTest;

public class KeystoreTest extends BaseIntegrationTest{

    @Test(priority = 1)
    public void testKeystore() throws InterruptedException{
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
            AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            KeystorePage keystorePage = authenticatedPage.goToKeystore();
            var modal = keystorePage.openNewKeystoreEntryModal();
            modal.create("test", "test", "test");
            keystorePage.search("test");
            var keystores = keystorePage.getActualKeystores();
            if(keystores.get(0).get("Alias").equals("test")){
                authenticatedPage.logout();
                Assertions.assertTrue(true);
            }
            else{
                Assertions.fail("keystore not found");
            }
        }
    }
}
