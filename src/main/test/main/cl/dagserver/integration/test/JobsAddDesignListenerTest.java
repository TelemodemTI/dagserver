package main.cl.dagserver.integration.test;

import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;

import lombok.extern.log4j.Log4j2;
import main.cl.dagserver.integration.pom.AuthenticatedPage;
import main.cl.dagserver.integration.pom.JobsPage;
import main.cl.dagserver.integration.pom.LoginPage;
import main.cl.dagserver.integration.pom.segments.EditorParamModal;
import main.cl.dagserver.integration.pom.segments.JobsUncompiledTab;
import main.cl.dagserver.integration.test.core.BaseIntegrationTest;

@Log4j2
public class JobsAddDesignListenerTest extends BaseIntegrationTest {

    @Test(priority = 1)
    public void addDesignListener() throws InterruptedException {
        log.info("addDesignListener");
        String jarname = "testing.jar";
        String dagname = "TEST_DAG";
        String group = "group.test";
        String listenerType = "onEnd";
        String triggerType = "GROUP";
        String nameTarget = "system_dags";
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
            canvas.createListenerDag(dagname, group,listenerType,triggerType,nameTarget);
            canvas.saveJar();
            authenticatedPage.goToJobs();
            authenticatedPage.logout();
            Assertions.assertTrue(true);
        }
    }
    
    @Test(priority = 2)
    public void editDesignListener() throws InterruptedException {
        log.info("editDesignListener");
        String jarname = "testing.jar";
        String step = "step1";
        String dagname = "TEST_DAG";
        String delimoriginal = "TEST";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
            AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            JobsPage jobsPage = authenticatedPage.goToJobs();
            JobsUncompiledTab uncompileds = jobsPage.goToUncompiledTab();
            uncompileds.searchUncompiled(jarname);
            var canvas = uncompileds.editDesign(jarname);
            canvas.selectDag(dagname);
            canvas.addStep(dagname,step,"main.cl.dagserver.infra.adapters.operators.FileOperator");
            EditorParamModal params = canvas.selectStage(step);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("mode", "read", "list");
            params.sendParameter("rowDelimiter", delimoriginal,"input");
            params.selectTab("//*[@id=\"file_li\"]/a");
            params.selectFile("filepath","//test.txt");
            params.save();
            canvas.save();
            canvas.close();
            authenticatedPage.goToJobs();
            authenticatedPage.logout();
            Assertions.assertTrue(true);
        }
    }

    @Test(priority = 3)
    public void deleteDesignListener() throws InterruptedException {
        log.info("deleteDesignListener");
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