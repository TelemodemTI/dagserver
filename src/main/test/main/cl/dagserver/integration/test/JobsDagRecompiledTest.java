package main.cl.dagserver.integration.test;

import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;
import main.cl.dagserver.integration.pom.AuthenticatedPage;
import main.cl.dagserver.integration.pom.JobsPage;
import main.cl.dagserver.integration.pom.LoginPage;
import main.cl.dagserver.integration.pom.segments.JobsCompiledTab;
import main.cl.dagserver.integration.pom.segments.JobsUncompiledTab;
import main.cl.dagserver.integration.test.core.BaseIntegrationTest;

public class JobsDagRecompiledTest  extends BaseIntegrationTest{
   
    @Test(priority = 1)
    public void importJob() throws InterruptedException{
        String jarname = "testing.jar";
        String uploadFileReal = "jarfile_json.json";
        String dagname = "generated_dag.main.DAG_pJAmLy";
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
    public void recompileJob() throws InterruptedException{
        String jarname = "testing.jar";
        String newjarname = "nuevo_nombre.jar";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
            AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            JobsPage jobsPage = authenticatedPage.goToJobs();
            JobsUncompiledTab uncompileds = jobsPage.goToUncompiledTab();
            uncompileds.searchUncompiled(jarname);
            var error = uncompileds.compileDesign(jarname);
            if(error != null){
                error.close();
                var canvas = uncompileds.editDesign(jarname);
                var renamer = canvas.renameModal();
                renamer.rename(newjarname);
                uncompileds = jobsPage.goToUncompiledTab();
                uncompileds.searchUncompiled(newjarname);
                error = uncompileds.compileDesign(newjarname);
                if(error != null){
                    error.close();
                    authenticatedPage.goToJobs();
                    authenticatedPage.logout();
                    Assertions.assertTrue(true);
                } else {
                    Assertions.fail("no se pudo obtener el error");
                }
            } else {
                Assertions.fail("no se pudo obtener el error");
            }
        }
    }

    @Test(priority = 3)
    public void recompileRenamed() throws InterruptedException{
        String jarname = "nuevo_nombre.jar";
        String dagname = "generated_dag.main.DAG_pJAmLy";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
            AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            JobsPage jobsPage = authenticatedPage.goToJobs();
            JobsCompiledTab compileds = jobsPage.goToCompiledTab();
            compileds.selectOption(dagname, 7);
            JobsUncompiledTab uncompileds = jobsPage.goToUncompiledTab();
            uncompileds.searchUncompiled(jarname);
            var error = uncompileds.compileDesign(jarname);
            if(error == null){
                compileds = jobsPage.goToCompiledTab();
                if(compileds.existJob(dagname)){
                    authenticatedPage.goToJobs();
                    authenticatedPage.logout();
                    Assertions.assertTrue(true);
                } else {
                    Assertions.fail("no se pudo compilar el dag?");
                }
            } else {
                Assertions.fail("no se pudo compilar el dag?");
            }
        }
    }
}