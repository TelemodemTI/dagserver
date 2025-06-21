package main.cl.dagserver.integration.test;

import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;

import main.cl.dagserver.integration.pom.AuthenticatedPage;
import main.cl.dagserver.integration.pom.JobsPage;
import main.cl.dagserver.integration.pom.LoginPage;
import main.cl.dagserver.integration.pom.segments.EditorParamModal;
import main.cl.dagserver.integration.pom.segments.JobsUncompiledTab;
import main.cl.dagserver.integration.test.core.BaseIntegrationTest;

public class JobsAddDesignCronTest extends BaseIntegrationTest{
    
    @Test(priority = 1)
    public void addDesignCron() throws InterruptedException {
        String jarname = "testing.jar";
        String dagname = "TEST_DAG";
        String group = "group.test";
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
            canvas.createCronDag(jarname,dagname, group, cronexpr);
            canvas.saveJar();
            authenticatedPage.goToJobs();
            authenticatedPage.logout();
            Assertions.assertTrue(true);
        }
    }

    @Test(priority = 2)
    public void editDesignCron() throws InterruptedException {
        String step1 = "step1";
        String jarname = "testing.jar";
        String dagname = "TEST_DAG";
        String delimoriginal = "TEST";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
            AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            JobsPage jobsPage = authenticatedPage.goToJobs();
            JobsUncompiledTab uncompileds = jobsPage.goToUncompiledTab();
            uncompileds.searchUncompiled(jarname);
            if(!uncompileds.existDesign(jarname)) {
                Assertions.fail("no se creo el dag?");
            }
            var canvas = uncompileds.editDesign(jarname);
            canvas.selectDag(dagname);
            canvas.addStep(dagname,step1,"main.cl.dagserver.infra.adapters.operators.FileOperator");
            EditorParamModal params = canvas.selectStage(step1);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("mode", "read", "list");
            params.sendParameter("rowDelimiter", delimoriginal,"input");
            params.selectTab("//*[@id=\"file_li\"]/a");
            params.selectFile("//test.txt");
            params.save();
            canvas.save();
            canvas.close();
            authenticatedPage.goToJobs();
            authenticatedPage.logout();
            Assertions.assertTrue(true);
        }
    }

    @Test(priority = 3)
    public void deleteDesignCron() throws InterruptedException {
        String step1 = "step1";
        String jarname = "testing.jar";
        String dagname = "TEST_DAG";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
            AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            JobsPage jobsPage = authenticatedPage.goToJobs();
            JobsUncompiledTab uncompileds = jobsPage.goToUncompiledTab();
            uncompileds.searchUncompiled(jarname);
            if(!uncompileds.existDesign(jarname)) {
                Assertions.fail("no se creo el dag?");
            }
            var canvas = uncompileds.editDesign(jarname);
            canvas.selectDag(dagname);
            var editor = canvas.selectStage(step1);
            editor.remove();
            canvas.save();
            canvas.close();
            authenticatedPage.goToJobs();
            authenticatedPage.logout();
            Assertions.assertTrue(true);
        }
    }
}
