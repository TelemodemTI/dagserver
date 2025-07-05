package main.cl.dagserver.integration.test;

import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;

import main.cl.dagserver.integration.pom.AuthenticatedPage;
import main.cl.dagserver.integration.pom.JobsPage;
import main.cl.dagserver.integration.pom.LoginPage;
import main.cl.dagserver.integration.pom.segments.JobsUncompiledTab;
import main.cl.dagserver.integration.test.core.BaseIntegrationTest;

public class JobsDesignCopyValuesTest extends BaseIntegrationTest{

    @Test(priority = 1)
    public void createDesign() throws InterruptedException {
        String jarname = "testing.jar";
        String dagname = "TEST_DAG";
        String group = "group.test";
        String step1 = "step1";
        String delimoriginal = "TEST";
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
            canvas.addStep(dagname,step1,"main.cl.dagserver.infra.adapters.operators.FileOperator");
            var editor = canvas.selectStage(step1);
            editor.selectTab("//*[@id=\"home_li\"]/a");
            editor.sendParameter("mode", "read", "list");
            editor.sendParameter("rowDelimiter", delimoriginal,"input");
            editor.selectTab("//*[@id=\"file_li\"]/a");
            editor.selectFile("filepath","//test.txt");
            editor.save();
            canvas.saveJar();
            authenticatedPage.goToJobs();
            authenticatedPage.logout();
            Assertions.assertTrue(true);
        }
    }

    @Test(priority = 2)
    public void copyValues() throws InterruptedException {
        String jarname = "testing.jar";
        String dagname = "TEST_DAG";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
            AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            JobsPage jobsPage = authenticatedPage.goToJobs();
            JobsUncompiledTab uncompileds = jobsPage.goToUncompiledTab();
            uncompileds.searchUncompiled(jarname);
            var canvas = uncompileds.editDesign(jarname);
            canvas.selectDag(dagname);
            canvas.copyProp(dagname);
            canvas.selectDag(dagname);
            canvas.copyOpts(dagname);
            canvas.close();
            authenticatedPage.goToJobs();
            authenticatedPage.logout();
            Assertions.assertTrue(true);
        }
    }

}
