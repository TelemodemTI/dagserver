package main.cl.dagserver.integration.test;

import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;

import lombok.extern.log4j.Log4j2;
import main.cl.dagserver.integration.pom.AuthenticatedPage;
import main.cl.dagserver.integration.pom.JobLogsPage;
import main.cl.dagserver.integration.pom.JobsPage;
import main.cl.dagserver.integration.pom.LoginPage;
import main.cl.dagserver.integration.pom.segments.JobsCompiledTab;
import main.cl.dagserver.integration.pom.segments.JobsUncompiledTab;
import main.cl.dagserver.integration.test.core.BaseIntegrationTest;
@Log4j2
public class JobsExecutionListenerTest extends BaseIntegrationTest {

    @Test(priority = 1)
    public void createListenerDag() throws InterruptedException {
        log.info("createListenerDag");
        String jarname = "testing.jar";
        String dagname = "TEST_DAG";
        String step = "step1";
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
            JobsCompiledTab compileds = jobsPage.goToCompiledTab();
            if(compileds.existJob(dagname)){
                compileds.selectOption(dagname, 7);
                authenticatedPage.goToJobs();
                uncompileds = jobsPage.goToUncompiledTab();
            }
            var canvas = jobsPage.createNewJobCanvas();
            canvas.setName(jarname);
            canvas.createListenerDag(dagname, group,listenerType,triggerType,nameTarget);
            canvas.selectDag(dagname);
            canvas.addStep(dagname,step,"main.cl.dagserver.infra.adapters.operators.DummyOperator");
            canvas.saveJar();
            authenticatedPage.goToJobs();
            authenticatedPage.logout();
            Assertions.assertTrue(true);
        }
    }
    
    @Test(priority = 2)
    public void compileDag() throws InterruptedException{
        log.info("compileDag");
        String jarname = "testing.jar";
        String dagname = "TEST_DAG";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
            AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            JobsPage jobsPage = authenticatedPage.goToJobs();
            JobsUncompiledTab uncompileds = jobsPage.goToUncompiledTab();
            uncompileds.compileDesign(jarname);
            JobsCompiledTab compileds = jobsPage.goToCompiledTab();
            if(compileds.existJob("generated_dag.main."+dagname)){
                authenticatedPage.goToJobs();
                authenticatedPage.logout();
                Assertions.assertTrue(true);
            } else {
                Assertions.fail("no se pudo compilar el dag?");
            }
        }
    }

    @Test(priority = 3)
    public void executeCronDag() throws InterruptedException{
        log.info("executeCronDag");
        String dagname = "TEST_DAG";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
            AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            JobsPage jobsPage = authenticatedPage.goToJobs();
            JobsCompiledTab compileds = jobsPage.goToCompiledTab();
            String realdagname = "generated_dag.main."+dagname;
            compileds.selectOption(realdagname, 4);
            Thread.sleep(90000);
            compileds.selectOption(realdagname, 3);
            JobLogsPage jobLogsPage = new JobLogsPage(this.driver);
            var data = jobLogsPage.getActualLogs();
            if(!data.isEmpty()) {
                var logdata1 = data.get(0);
                jobLogsPage.viewLog(logdata1.get("Id"));
                authenticatedPage.goToJobs();
                authenticatedPage.logout();
                Assertions.assertTrue(true);
            } else {
                Assertions.fail("no se ejecuto cron!");
            }
        }
    }
}
