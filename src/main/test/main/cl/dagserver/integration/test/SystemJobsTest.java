package main.cl.dagserver.integration.test;

import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;

import lombok.extern.log4j.Log4j2;
import main.cl.dagserver.integration.pom.AuthenticatedPage;
import main.cl.dagserver.integration.pom.JarDetailPage;
import main.cl.dagserver.integration.pom.JobDependenciesPage;
import main.cl.dagserver.integration.pom.JobLogsPage;
import main.cl.dagserver.integration.pom.JobsPage;
import main.cl.dagserver.integration.pom.LoginPage;
import main.cl.dagserver.integration.pom.segments.JobsCompiledTab;
import main.cl.dagserver.integration.test.core.BaseIntegrationTest;


@Log4j2
public class SystemJobsTest extends BaseIntegrationTest {

    @Test(priority = 1)
    public void getDetailSystemJobs() throws InterruptedException {
        log.info("getDetailSystemJobs");
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
            AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            JobsPage jobsPage = authenticatedPage.goToJobs();
            JobsCompiledTab compileds = jobsPage.goToCompiledTab();
            if(compileds.existJob("background_system_dag") && compileds.existJob("event_system_dag")) {
                compileds.selectOption("background_system_dag", 1);
                JarDetailPage jarDetailPage = new JarDetailPage(this.driver);
                var modal = jarDetailPage.selectStage("background_system_dag", "internal");
                modal.close();
                authenticatedPage.goToJobs();
                authenticatedPage.logout();
                Assertions.assertTrue(true);
            } else {
                Assertions.fail("no existen los jobs de sistema¿?");
            }
        }
    }

    @Test(priority = 2)
    public void getLogsSystemJobs() throws InterruptedException {
        log.info("getLogsSystemJobs");
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
            AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            JobsPage jobsPage = authenticatedPage.goToJobs();
            JobsCompiledTab compileds = jobsPage.goToCompiledTab();
            Thread.sleep(10000); //esperar a que se ejecute el proceso de sistema
            compileds.selectOption("background_system_dag", 2);
            JobLogsPage jobLogsPage = new JobLogsPage(this.driver);
            var data = jobLogsPage.getActualLogs();
            if(!data.isEmpty()) {
                var logdata = data.get(0);
                jobLogsPage.viewLog(logdata.get("Id"));
                authenticatedPage.goToJobs();
                jobsPage.goToCompiledTab();
                compileds.selectOption("background_system_dag", 2);
                jobLogsPage.deleteById(logdata.get("Id"));
                if(!jobLogsPage.existLog(logdata.get("Id"))) {
                    authenticatedPage.goToJobs();
                    authenticatedPage.logout();
                    Assertions.assertTrue(true);
                } else {
                    Assertions.fail("no se pudo eliminar el log");
                }
            } else {
                Assertions.fail("no existen logs del job de sistema¿?");
            }
        }
    }

    @Test(priority = 3)
    public void getDependenciesSystemJobs() throws InterruptedException {
        log.info("getDependenciesSystemJobs");
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
            AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            JobsPage jobsPage = authenticatedPage.goToJobs();
            JobsCompiledTab compileds = jobsPage.goToCompiledTab();
            Thread.sleep(60000); //esperar a que se ejecute el proceso de sistema
            compileds.selectOption("background_system_dag", 2);
            new JobDependenciesPage(this.driver);
            authenticatedPage.goToJobs();
            authenticatedPage.logout();
            Assertions.assertTrue(true);
        }
    }
}
