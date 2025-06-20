package main.cl.dagserver.integration.test;



import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;

import main.cl.dagserver.integration.pom.AuthenticatedPage;
import main.cl.dagserver.integration.pom.JarDetailPage;
import main.cl.dagserver.integration.pom.JobLogsPage;
import main.cl.dagserver.integration.pom.JobsPage;
import main.cl.dagserver.integration.pom.LoginPage;
import main.cl.dagserver.integration.pom.segments.JarPreExecutionModal;
import main.cl.dagserver.integration.pom.segments.JobsCompiledTab;
import main.cl.dagserver.integration.pom.segments.JobsUncompiledTab;
import main.cl.dagserver.integration.test.core.BaseIntegrationTest;


public class JobsDagCompilationTest extends BaseIntegrationTest{

    @Test(priority = 1)
    public void importAndCompileDag() throws InterruptedException {
        String jarname = "testing.jar";
        String uploadFileReal = "jarfile_json.json";
        String dagname = "generated_dag.main.DAG_TEST";
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
            var modal = uncompileds.importJarModal();
            modal.importDesign(uploadFileReal);
            authenticatedPage.goToJobs();
            uncompileds = jobsPage.goToUncompiledTab();
            uncompileds.compileDesign(jarname);
            authenticatedPage.goToJobs();
            authenticatedPage.logout();
            Assertions.assertTrue(true);
        }
    }

    @Test(priority = 2)
    public void validateExecution() throws InterruptedException {
        
        String dagname = "generated_dag.main.DAG_TEST";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
            AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            JobsPage jobsPage = authenticatedPage.goToJobs();
            JobsCompiledTab compileds = jobsPage.goToCompiledTab();
            compileds.selectOption(dagname, 4);
            String rv = compileds.getSchedulerActive(dagname);
            if(Boolean.parseBoolean(rv)) {
                compileds.selectOption(dagname, 4);
                compileds.selectOption(dagname, 1);
                var modal = new JarPreExecutionModal(driver);
                var resultmodal = modal.save();
                resultmodal.close();
                compileds.selectOption(dagname, 1);
                modal = new JarPreExecutionModal(driver);
                resultmodal = modal.save();
                resultmodal.close();
                compileds.selectOption(dagname, 3);  
                JobLogsPage jobLogsPage = new JobLogsPage(this.driver);
                var data = jobLogsPage.getActualLogs();
                if(!data.isEmpty()) {
                    var logdata = data.get(0);
                    jobLogsPage.viewLog(logdata.get("Id"));
                    authenticatedPage.goToJobs();
                    authenticatedPage.logout();
                    Assertions.assertTrue(true);
                } else {
                    Assertions.fail("no se pudo obtener los logs");
                }
            } else {
                Assertions.fail("no se pudo el status del scheduler");
            }
        }
    }

    @Test(priority = 3)
    public void deleteLog() throws InterruptedException{
        String dagname = "generated_dag.main.DAG_TEST";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
            AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            JobsPage jobsPage = authenticatedPage.goToJobs();
            JobsCompiledTab compileds = jobsPage.goToCompiledTab();
            compileds.selectOption(dagname, 3);  
            JobLogsPage jobLogsPage = new JobLogsPage(this.driver);
            var data = jobLogsPage.getActualLogs();
            if(!data.isEmpty()) {
                var logdata = data.get(0);
                jobLogsPage.deleteById(logdata.get("Id"));
                authenticatedPage.goToJobs();
                authenticatedPage.logout();
                Assertions.assertTrue(true);
            } else {
                Assertions.fail("no se pudo obtener los logs");
            }
        }
    }

    @Test(priority = 4)
    public void deleteAllLogs() throws InterruptedException{
        String dagname = "generated_dag.main.DAG_TEST";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
            AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            JobsPage jobsPage = authenticatedPage.goToJobs();
            JobsCompiledTab compileds = jobsPage.goToCompiledTab();
            compileds.selectOption(dagname, 3);  
            JobLogsPage jobLogsPage = new JobLogsPage(this.driver);
            jobLogsPage.deleteAll();
            var data = jobLogsPage.getActualLogs();
            if(data.isEmpty()) {
                authenticatedPage.goToJobs();
                authenticatedPage.logout();
                Assertions.assertTrue(true);
            } else {
                Assertions.fail("no se pudo eliminar los logs");
            }
        }
    }

    @Test(priority = 5)
    public void validateDetail() throws InterruptedException{
        String dagname = "generated_dag.main.DAG_TEST";
        String stepname = "step1";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
            AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            JobsPage jobsPage = authenticatedPage.goToJobs();
            JobsCompiledTab compileds = jobsPage.goToCompiledTab();
            compileds.selectOption(dagname, 2);  
            JarDetailPage jarDetailPage = new JarDetailPage(this.driver);
            var modal = jarDetailPage.selectStage(dagname, stepname);
            modal.close();
            authenticatedPage.goToJobs();
            authenticatedPage.logout();
            Assertions.assertTrue(true);
        }
    }
}
