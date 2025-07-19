package main.cl.dagserver.integration.test;

import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;

import lombok.extern.log4j.Log4j2;
import main.cl.dagserver.integration.pom.AuthenticatedPage;
import main.cl.dagserver.integration.pom.JobsPage;
import main.cl.dagserver.integration.pom.LoginPage;
import main.cl.dagserver.integration.pom.segments.CanvasDagEditor;
import main.cl.dagserver.integration.pom.segments.EditorParamModal;
import main.cl.dagserver.integration.pom.segments.JobsUncompiledTab;
import main.cl.dagserver.integration.test.core.BaseOperatorTest;

@Log4j2
public class OperatorCmdTest extends BaseOperatorTest {

    @Test(priority = 1)
    public void executeCmd() throws InterruptedException {
        log.info("executeCmd");
        String dagname = "TEST_FILE1_DAG";
        String step = "step1";
        String group = "group.test";
        String jarname = "filetest1.jar";
        String cmd1 = "java -version";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
        	JobsPage jobsPage = authenticatedPage.goToJobs();
        	CanvasDagEditor canvas = createJob(jobsPage,jarname, dagname, group);
            canvas.addStep(dagname,step,"main.cl.dagserver.infra.adapters.operators.CmdOperator");
            EditorParamModal params = canvas.selectStage(step);
            params.selectTab("//*[@id=\"profile_li\"]/a");
            params.sendScript(cmd1);
            params.save();
            canvas.saveJar();
            jobsPage = authenticatedPage.goToJobs();
            var status = executeDesign(step, jarname, dagname,jobsPage);
            if(!status.isEmpty()) {
            	String output = status.getJSONObject(0).get("output").toString();
                if(!output.isEmpty()) {
                    authenticatedPage.goToJobs();
                    authenticatedPage.logout();
                    Assertions.assertTrue(true);
                } else {
                    Assertions.fail("Problema al ejecutar el operador?");
                }
            }
        }
    }
    
    @Test(priority = 2)
    public void canBeExecutedInGroovyTest() throws InterruptedException {
        log.info("canBeExecutedInGroovyTest");
    	String dagname = "TEST_EXECUTED_BY_GROOVY_DAG";
        String step = "step1";
        String group = "group.test";
        String jarname = "canBeExecutedInGroovyTest.jar";
        String cmd1 = "def args = new Properties();def optionals = new Properties();" +
                  "args.setProperty(\"cmd\",\"java -version\" );"+
                  "return operator.setArgs(args).setOptionals(optionals).setOperator(\"CmdOperator\").execute()";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
        	JobsPage jobsPage = authenticatedPage.goToJobs();
        	this.createGroovyJob(jobsPage, dagname, step, group, jarname, cmd1);
        	jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step, jarname, dagname,jobsPage);
        	if(!status.isEmpty()) {
        		String output = status.getJSONObject(0).get("output").toString();
        		if(!output.isEmpty()) {
        			authenticatedPage.goToJobs();
                    authenticatedPage.logout();
        			Assertions.assertTrue(true);
        		} else {
        			Assertions.fail("Problema al ejecutar el operador?");
        		}
        	}
        }
    }

    @Test(priority = 3)
    public void cmdCanBeOutputStepTest() throws InterruptedException {
    	log.info("cmdCanBeOutputStepTest");
    	String dagname = "TEST_FILE1_DAG";
        String step1 = "step0";
        String step2 = "step1";
        String group = "group.test";
        String jarname = "filetest2.jar";
        String cmd1 = "return \"java -version\"";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
        	JobsPage jobsPage = authenticatedPage.goToJobs();
        	createGroovyJob(jobsPage, dagname, step1, group, jarname, cmd1);
        	
        	jobsPage = authenticatedPage.goToJobs();
            JobsUncompiledTab uncompileds = jobsPage.goToUncompiledTab();
            uncompileds.searchUncompiled(jarname);
            CanvasDagEditor canvas = uncompileds.editDesign(jarname);
            canvas.selectDag(dagname);

            canvas.addStep(dagname,step2,"main.cl.dagserver.infra.adapters.operators.CmdOperator");
            EditorParamModal params = canvas.selectStage(step2);
            params.selectTab("//*[@id=\"profile_li\"]/a");
            params.sendScript("${step0}");
            params.save();
            canvas.save();
            canvas.close();
        	jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step2, jarname, dagname,jobsPage);
        	if(!status.isEmpty()) {
        		String output = status.getJSONObject(0).get("output").toString();
        		if(!output.isEmpty()) {
        			authenticatedPage.goToJobs();
                    authenticatedPage.logout();
        			Assertions.assertTrue(true);
        		} else {
        			Assertions.fail("Problema al ejecutar el operador?");
        		}
        	} else {
        		Assertions.fail("Problema al ejecutar el operador?");
        	}
        }
    	
    }
}
