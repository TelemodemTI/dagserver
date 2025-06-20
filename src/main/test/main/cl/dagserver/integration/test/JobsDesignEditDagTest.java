package main.cl.dagserver.integration.test;

import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;

import main.cl.dagserver.integration.pom.AuthenticatedPage;
import main.cl.dagserver.integration.pom.JobsPage;
import main.cl.dagserver.integration.pom.LoginPage;
import main.cl.dagserver.integration.pom.segments.JobsUncompiledTab;
import main.cl.dagserver.integration.test.core.BaseIntegrationTest;

public class JobsDesignEditDagTest extends BaseIntegrationTest{

    @Test(priority = 1)
    public void createDag() throws InterruptedException {
        String jarname = "testing.jar";
        String dagname = "TEST_DAG";
        String group = "group.test";
        String step1 = "step1";
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
            canvas.createNoneDag(dagname, group);
            canvas.addStep(dagname,step1,"main.cl.dagserver.infra.adapters.operators.DummyOperator");
            canvas.selectDag(dagname);
            canvas.saveJar();
            authenticatedPage.goToJobs();
            authenticatedPage.logout();
            Assertions.assertTrue(true);
        }
    }

    @Test(priority = 2)
    public void validateExecution() throws InterruptedException {
        String jarname = "testing.jar";
        String newjarname = "testing1.jar";
        String dagname = "TEST_DAG";
        String stepname = "step1";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
            AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            JobsPage jobsPage = authenticatedPage.goToJobs();
            JobsUncompiledTab uncompileds = jobsPage.goToUncompiledTab();
            uncompileds.searchUncompiled(jarname);
            var canvas = uncompileds.editDesign(jarname);
            if(canvas.execute() == null) {
                canvas.selectDag(dagname);
                var argsEditor = canvas.execute();
                var resultModal =argsEditor.save();
                resultModal.close();
                var renameModel =canvas.renameModal();
                renameModel.rename(newjarname);
                uncompileds = jobsPage.goToUncompiledTab();
                canvas = uncompileds.editDesign(newjarname);
                canvas.selectDag(dagname);
                canvas.selectStage(stepname);    	
                Thread.sleep(2000);
                argsEditor = canvas.test();
                resultModal =argsEditor.save();
                resultModal.close();
                authenticatedPage.goToJobs();
                authenticatedPage.logout();
                Assertions.assertTrue(true);
            }
        }
    }
}
