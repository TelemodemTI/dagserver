package main.cl.dagserver.integration.test;

import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;

import main.cl.dagserver.integration.pom.AuthenticatedPage;
import main.cl.dagserver.integration.pom.JobLogsPage;
import main.cl.dagserver.integration.pom.JobsPage;
import main.cl.dagserver.integration.pom.LoginPage;
import main.cl.dagserver.integration.pom.segments.JobsCompiledTab;
import main.cl.dagserver.integration.pom.segments.JobsUncompiledTab;
import main.cl.dagserver.integration.test.core.BaseIntegrationTest;

public class JobsEventBubbleCronTest extends BaseIntegrationTest {

    @Test(priority = 1)
    public void createFirstDag() throws InterruptedException {
        String jarname = "testing.jar";
        String dagname = "DAG_TEST1";
        String group = "first_group";
        String cronexpr = "0 0/1 * ? * *";
        String step = "step1";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
            AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            JobsPage jobsPage = authenticatedPage.goToJobs();
            JobsCompiledTab compileds = jobsPage.goToCompiledTab();
            if(compileds.existJob(dagname)){
                compileds.selectOption(dagname, 7);
                authenticatedPage.goToJobs();
            }
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
            canvas.selectDag(dagname);
            canvas.addStep(dagname,step,"main.cl.dagserver.infra.adapters.operators.DummyOperator");
            canvas.saveJar();
            authenticatedPage.goToJobs();
            uncompileds = jobsPage.goToUncompiledTab();
            uncompileds.compileDesign(jarname);
            authenticatedPage.goToJobs();
            authenticatedPage.logout();
            Assertions.assertTrue(true);
        }
    }
    @Test(priority = 2)
    public void createSecondDag() throws InterruptedException {
        String jarname = "testing2.jar";
        String dagname = "DAG_TEST2";
        String targetGroup = "first_group";
        String group = "second_group";
        String step = "step1";
        String listenerType = "onEnd";
        String triggerType = "GROUP";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
            AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            JobsPage jobsPage = authenticatedPage.goToJobs();
            JobsCompiledTab compileds = jobsPage.goToCompiledTab();
            if(compileds.existJob(dagname)){
                compileds.selectOption(dagname, 7);
                authenticatedPage.goToJobs();
            }
            JobsUncompiledTab uncompileds = jobsPage.goToUncompiledTab();
            uncompileds.searchUncompiled(jarname);
            if(uncompileds.existDesign(jarname)) {
                uncompileds.deleteDesign(jarname);
                authenticatedPage.goToJobs();
                uncompileds = jobsPage.goToUncompiledTab();
            }
            var canvas = jobsPage.createNewJobCanvas();
            canvas.setName(jarname);
            canvas.createListenerDag(dagname, group,listenerType,triggerType,targetGroup);
            canvas.selectDag(dagname);
            canvas.addStep(dagname,step,"main.cl.dagserver.infra.adapters.operators.DummyOperator");
            canvas.saveJar();
            authenticatedPage.goToJobs();
            uncompileds = jobsPage.goToUncompiledTab();
            uncompileds.compileDesign(jarname);
            authenticatedPage.goToJobs();
            authenticatedPage.logout();
            Assertions.assertTrue(true);
        }
    }

    @Test(priority = 3)
    public void createThirdDag() throws InterruptedException {
        String jarname = "testing3.jar";
        String dagname = "DAG_TEST3";
        String targetGroup = "second_group";
        String group = "third_group";
        String step = "step1";
        String listenerType = "onEnd";
        String triggerType = "GROUP";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
            AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            JobsPage jobsPage = authenticatedPage.goToJobs();
            JobsCompiledTab compileds = jobsPage.goToCompiledTab();
            if(compileds.existJob(dagname)){
                compileds.selectOption(dagname, 7);
                authenticatedPage.goToJobs();
            }
            JobsUncompiledTab uncompileds = jobsPage.goToUncompiledTab();
            uncompileds.searchUncompiled(jarname);
            if(uncompileds.existDesign(jarname)) {
                uncompileds.deleteDesign(jarname);
                authenticatedPage.goToJobs();
                uncompileds = jobsPage.goToUncompiledTab();
            }
            var canvas = jobsPage.createNewJobCanvas();
            canvas.setName(jarname);
            canvas.createListenerDag(dagname, group,listenerType,triggerType,targetGroup);
            canvas.selectDag(dagname);
            canvas.addStep(dagname,step,"main.cl.dagserver.infra.adapters.operators.DummyOperator");
            canvas.saveJar();
            authenticatedPage.goToJobs();
            uncompileds = jobsPage.goToUncompiledTab();
            uncompileds.compileDesign(jarname);
            authenticatedPage.goToJobs();
            authenticatedPage.logout();
            Assertions.assertTrue(true);
        }
    }
    @Test(priority = 4)
    public void testEventBubble() throws InterruptedException {
        String dagname1 = "generated_dag.main.DAG_TEST1";
        String dagname2 = "generated_dag.main.DAG_TEST2";
        String dagname3 = "generated_dag.main.DAG_TEST3";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
            AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            JobsPage jobsPage = authenticatedPage.goToJobs();
            JobsCompiledTab compileds = jobsPage.goToCompiledTab();
            compileds.selectOption(dagname1, 4);
            compileds.selectOption(dagname2, 4);
            compileds.selectOption(dagname3, 4);
            Thread.sleep(90000);
            compileds.selectOption(dagname1, 3);  
            JobLogsPage jobLogsPage = new JobLogsPage(this.driver);
            var data1 = jobLogsPage.getActualLogs();
            if(data1.get(0).get("Id").equals("No data available in table")) {
                Assertions.fail("no se ejecuto cron del dag 1");
            }
            jobsPage = authenticatedPage.goToJobs();
            compileds = jobsPage.goToCompiledTab();
            compileds.selectOption(dagname2, 3);
            jobLogsPage = new JobLogsPage(this.driver);
            var data2 = jobLogsPage.getActualLogs();
            if(data2.get(0).get("Id").equals("No data available in table")) {
                Assertions.fail("no se ejecuto listener del dag 2");
            }
            jobsPage = authenticatedPage.goToJobs();
            compileds = jobsPage.goToCompiledTab();
            compileds.selectOption(dagname3, 3);
            jobLogsPage = new JobLogsPage(this.driver);
            var data3 = jobLogsPage.getActualLogs();
            if(data3.get(0).get("Id").equals("No data available in table")) {
                Assertions.fail("no se ejecuto listener del dag 3");
            }
            authenticatedPage.goToJobs();
            authenticatedPage.logout();
            Assertions.assertTrue(true);
        }
    }
}
