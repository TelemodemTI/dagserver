package main.cl.dagserver.integration.test;

import org.json.JSONArray;
import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;

import main.cl.dagserver.integration.pom.AuthenticatedPage;
import main.cl.dagserver.integration.pom.JobsPage;
import main.cl.dagserver.integration.pom.LoginPage;
import main.cl.dagserver.integration.pom.segments.EditorParamModal;
import main.cl.dagserver.integration.pom.segments.JobsUncompiledTab;
import main.cl.dagserver.integration.test.core.BaseIntegrationTest;

public class JobsExecOkErrorSiTest extends BaseIntegrationTest {

    @Test(priority = 1)
    public void addDesignStartGroovy() throws InterruptedException {

            String jarname = "testing.jar";
            String dagname = "TEST_DAG";
            String group = "group.test";
            String step = "step1";
            LoginPage loginPage = new LoginPage(this.driver);
            if(loginPage.login("dagserver", "dagserver")){
                AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
                JobsPage jobsPage = authenticatedPage.goToJobs();
                var canvas = jobsPage.createNewJobCanvas();
                canvas.setName(jarname);
                canvas.createNoneDag(dagname, group);
                canvas.selectDag(dagname);
                canvas.addStep(dagname,step,"main.cl.dagserver.infra.adapters.operators.GroovyOperator");
                var cmd1 = "return new DomainException(new Exception(\""+step+"\"));";
                EditorParamModal params = canvas.selectStage(step);
                params.selectTab("//*[@id=\"profile_li\"]/a");
                params.sendScript(cmd1);
                params.save();
                canvas.saveJar();
                authenticatedPage.goToJobs();
                authenticatedPage.logout();
                Assertions.assertTrue(true);
            }
    }

    @Test(priority = 2)
    public void addGroovyStep() throws InterruptedException {
        String dagname = "TEST_DAG";
        String step = "step2";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
            AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            JobsPage jobsPage = authenticatedPage.goToJobs();
            JobsUncompiledTab uncompileds = jobsPage.goToUncompiledTab();
            uncompileds.searchUncompiled("testing.jar");
            var canvas = uncompileds.editDesign("testing.jar");
            canvas.selectDag(dagname);
            canvas.addStep(dagname,step,"main.cl.dagserver.infra.adapters.operators.GroovyOperator");
            String cmd1 = "return \""+step+"\"";
            EditorParamModal params = canvas.selectStage(step);
            params.selectTab("//*[@id=\"profile_li\"]/a");
            params.sendScript(cmd1);
            params.selectTab("//*[@id=\"settings_li\"]/a");
            params.setInputStatusType("ERROR");
            params.save();
            canvas.save();
            authenticatedPage.goToJobs();
            authenticatedPage.logout();
            Assertions.assertTrue(true);
        }
    }

    @Test(priority = 3)
    public void executeDag() throws InterruptedException {
        String jarname = "testing.jar";
        String dagname = "TEST_DAG";
        String step = "step2";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
            AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            JobsPage jobsPage = authenticatedPage.goToJobs();
            JobsUncompiledTab uncompileds = jobsPage.goToUncompiledTab();
            uncompileds.searchUncompiled(jarname);
            var canvas = uncompileds.editDesign(jarname);
            canvas.selectDag(dagname);
            var modal = canvas.execute();
            var result = modal.save();
            String jsonarrstr = result.getOutputXcom(step);
            var contentPrc2 = new JSONArray(jsonarrstr.trim());
            Boolean founded = false;
            for (int i = 0; i < contentPrc2.length(); i++) {
                var obj = contentPrc2.getJSONObject(i);
                var str = obj.getString("output");
                if(str.equals(step)) {
                    founded = true;
                    break;
                }
            }
            if(!founded) {
                Assertions.fail("no se ejecuto de forma esperada");
            }
            result.close();
            authenticatedPage.goToJobs();
            authenticatedPage.logout();
            Assertions.assertTrue(true);
        }
    }
}
