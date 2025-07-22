package main.cl.dagserver.integration.test;

import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;

import lombok.extern.log4j.Log4j2;
import main.cl.dagserver.integration.pom.AuthenticatedPage;
import main.cl.dagserver.integration.pom.JobsPage;
import main.cl.dagserver.integration.pom.LoginPage;
import main.cl.dagserver.integration.pom.segments.JobsUncompiledTab;
import main.cl.dagserver.integration.test.core.BaseIntegrationTest;

@Log4j2
public class JobsMultiDagTest extends BaseIntegrationTest{

    @Test(priority = 1)
    public void createMultiDag() throws InterruptedException{
        log.info("createMultiDag");
        String jarname = "testing.jar";
        String dagname1 = "DAG_TEST";
        String group = "group.test";
        String step = "step1";
        String cronexpr = "0 0/1 * * * ?";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
            AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            JobsPage jobsPage = authenticatedPage.goToJobs();
            JobsUncompiledTab uncompileds = jobsPage.goToUncompiledTab();
            uncompileds.searchUncompiled(jarname);
            if(uncompileds.existDesign(jarname)) {
                uncompileds.deleteDesign(jarname);
                authenticatedPage.goToJobs();
                uncompileds = jobsPage.goToUncompiledTab();
            }
            var canvas = jobsPage.createNewJobCanvas();
            canvas.setName(jarname);
            canvas.createCronDag(jarname,dagname1, group, cronexpr);
            canvas.selectDag(dagname1);
            canvas.addStep(dagname1, step, "main.cl.dagserver.infra.adapters.operators.DummyOperator");
            String dagname2 = canvas.createTmpDagNone();
            canvas.selectDag(dagname2);
            canvas.addStep(dagname2, step, "main.cl.dagserver.infra.adapters.operators.DummyOperator");
            canvas.saveJar();
            authenticatedPage.goToJobs();
            authenticatedPage.logout();
            Assertions.assertTrue(true);
        }
    }

}
