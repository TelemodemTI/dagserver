package main.cl.dagserver.integration.test;

import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;

import lombok.extern.log4j.Log4j2;
import main.cl.dagserver.integration.pom.AuthenticatedPage;
import main.cl.dagserver.integration.pom.KeystorePage;
import main.cl.dagserver.integration.pom.LoginPage;
import main.cl.dagserver.integration.test.core.BaseIntegrationTest;

@Log4j2
public class KeystoreTest extends BaseIntegrationTest{

    @Test(priority = 1)
    public void testKeystore() throws InterruptedException{
        log.info("testKeystore");
        String keystoreAlias = "test";
        String keystoreUsername = "test";
        String keystorePassword = "test";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
            AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            KeystorePage keystorePage = authenticatedPage.goToKeystore();
            var modal = keystorePage.openNewKeystoreEntryModal();
            modal.create(keystoreAlias, keystoreUsername, keystorePassword);
            keystorePage.search(keystoreAlias);
            var keystores = keystorePage.getActualKeystores();
            if(keystores.get(0).get("Alias").equals(keystoreAlias)){
                authenticatedPage.logout();
                Assertions.assertTrue(true);
            }
            else{
                Assertions.fail("keystore not found");
            }
        }
    }

    @Test(priority = 2)
    public void exportKeystore() throws InterruptedException{
        log.info("exportKeystore");
        String keystoreAlias = "test";
        String jksFile = "keystore.jks";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
            AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            KeystorePage keystorePage = authenticatedPage.goToKeystore();
            keystorePage.downloadKeystore();
            keystorePage.deleteKeystore(keystoreAlias);
            var modal = keystorePage.openImportKeystoreModal();
            modal.importKeystore(jksFile);
            keystorePage = authenticatedPage.goToKeystore();
            keystorePage.search(keystoreAlias);
            var keystores = keystorePage.getActualKeystores();
            if(keystores.get(0).get("Alias").equals(keystoreAlias)){
                authenticatedPage.logout();
                Assertions.assertTrue(true);
            }
            else{
                Assertions.fail("keystore not found");
            }
        }
    }
}
