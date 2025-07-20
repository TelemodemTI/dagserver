package main.cl.dagserver.integration.test;

import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;

import lombok.extern.log4j.Log4j2;
import main.cl.dagserver.integration.pom.AuthenticatedPage;
import main.cl.dagserver.integration.pom.JobsPage;
import main.cl.dagserver.integration.pom.LoginPage;
import main.cl.dagserver.integration.pom.segments.EditorParamModal;
import main.cl.dagserver.integration.pom.segments.JobsUncompiledTab;
import main.cl.dagserver.integration.test.core.BaseOperatorTest;

@Log4j2
public class OperatorGroovyTest extends BaseOperatorTest {

    @Test(priority = 1)
    public void okBasicTest() throws InterruptedException {
        log.info("okBasicTest");
        String dagname = "TEST_BASIC_DAG";
        String step = "step1";
        String group = "group.test";
        String jarname = "okBasicTest.jar";
        String cmd1 = "return \""+step+"\"";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
        	JobsPage jobsPage = authenticatedPage.goToJobs();
        	this.createGroovyJob(jobsPage, dagname, step, group, jarname, cmd1);
        	jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step, jarname, dagname,jobsPage);
        	if(!status.isEmpty()) {
        		if(status.getJSONObject(0).get("output").equals(step)) {
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
    public void okListTest() throws InterruptedException {
		log.info("okListTest");
    	String dagname = "TEST_LIST_DAG";
        String step = "step1";
        String group = "group.test";
        String jarname = "okListTest.jar";
        var cmd = "return [\"uno\",\"dos\"];";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
        	JobsPage jobsPage = authenticatedPage.goToJobs();
        	this.createGroovyJob(jobsPage, dagname, step, group, jarname, cmd);
        	jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step, jarname, dagname,jobsPage);
        	if(!status.isEmpty()) {
				if(status.length() == 2) {
					var jretu = status.getJSONObject(0);
					if(jretu.get("content").equals("uno")) {
						authenticatedPage.goToJobs();
	                    authenticatedPage.logout();
	        			Assertions.assertTrue(true);
					}
				} else {
        			Assertions.fail("Problema al ejecutar el operador?");
        		}
        	}
        }
    }
    
    
    
    
    @Test(priority = 3)
    public void okMapTest() throws InterruptedException {
		log.info("okMapTest");
    	String dagname = "TEST_MAP_DAG";
        String step = "step1";
        String group = "group.test";
        String jarname = "okMapTest.jar";
        var cmd = "return [\"key\":\"value\"];";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
        	JobsPage jobsPage = authenticatedPage.goToJobs();
        	this.createGroovyJob(jobsPage, dagname, step, group, jarname, cmd);
        	jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step, jarname, dagname,jobsPage);
        	if(!status.isEmpty()) {
				if(status.length() == 1) {
					var jretu = status.getJSONObject(0);
					if(jretu.get("key").equals("value")) {
						authenticatedPage.goToJobs();
	                    authenticatedPage.logout();
	        			Assertions.assertTrue(true);
					}
				} else {
        			Assertions.fail("Problema al ejecutar el operador?");
        		}
        	}
        }
    }
    
    @Test(priority = 4)
    public void okListMapTest() throws InterruptedException {
		log.info("okListMapTest");
        String dagname = "TEST_MAP_LIST_DAG";
        String step = "step1";
        String group = "group.test";
        String jarname = "okListMapTest.jar";
        var cmd = "return [[\"key\":\"value\"],[\"key\":\"value2\"]];";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
        	JobsPage jobsPage = authenticatedPage.goToJobs();
        	this.createGroovyJob(jobsPage, dagname, step, group, jarname, cmd);
        	jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step, jarname, dagname,jobsPage);
        	if(!status.isEmpty()) {
				if(status.length() == 2) {
					var jretu = status.getJSONObject(0);
					if(jretu.get("key").equals("value")) {
						authenticatedPage.goToJobs();
	                    authenticatedPage.logout();
	        			Assertions.assertTrue(true);
					}
				} else {
        			Assertions.fail("Problema al ejecutar el operador?");
        		}
        	}
        }
    }
    
    
    @Test(priority = 5)
    public void okNumberTest() throws InterruptedException {
		log.info("okNumberTest");
        String dagname = "TEST_NUMBER_DAG";
        String step = "step1";
        String group = "group.test";
        String jarname = "okNumberTest.jar";
        var cmd = "return 1;";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
        	JobsPage jobsPage = authenticatedPage.goToJobs();
        	this.createGroovyJob(jobsPage, dagname, step, group, jarname, cmd);
        	jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step, jarname, dagname,jobsPage);
        	if(!status.isEmpty()) {
				if(status.length() == 1) {
					var jretu = status.getJSONObject(0);
					if(jretu.get("output").equals(1)) {
						authenticatedPage.goToJobs();
	                    authenticatedPage.logout();
	        			Assertions.assertTrue(true);
					}
				} else {
        			Assertions.fail("Problema al ejecutar el operador?");
        		}
        	}
        }
    }
    

    @Test(priority = 6)
    public void okUncompiledTest() throws InterruptedException {
		log.info("okUncompiledTest");
        String dagname = "TEST_UNCOMPILED_DAG";
        String dagnameExec = "TEST_UNCOMPILED_TOBE_DAG";
        String step = "step1";
        String group = "group.test";
        String jarname = "okUncompiledTest.jar";
		String jarnameExec = "okUncompiledToBeTest.jar";
        var cmd = "return \"testing\";";
		StringBuilder source = new StringBuilder("dag.isCompiled(false).setArgs(\"{}\",\"{}\").execute(\""+jarnameExec+"\",\"generated_dag.main."+dagnameExec+"\");\n");
		source.append("return \"test\";");
		var cmdFinal = source.toString();
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
        	JobsPage jobsPage = authenticatedPage.goToJobs();
            this.createGroovyJob(jobsPage, dagnameExec, step, group, jarnameExec, cmd);
            JobsUncompiledTab tab = jobsPage.goToUncompiledTab();
			tab.compileDesign(jarname);
			jobsPage = authenticatedPage.goToJobs();
        	this.createGroovyJob(jobsPage, dagname, step, group, jarname, cmdFinal);
        	jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step, jarname, dagname,jobsPage);
			if(!status.isEmpty()) {
				if(status.length() == 1) {
					var jretu = status.getJSONObject(0);
					if(jretu.get("output").equals("test")) {
						authenticatedPage.goToJobs();
	                    authenticatedPage.logout();
	        			Assertions.assertTrue(true);
					}
				} else {
        			Assertions.fail("Problema al ejecutar el operador?");
        		}
			}
        }
    }

    
    @Test(priority = 7)
    public void okCompiledTest() throws InterruptedException {
		log.info("okCompiledTest");
        String dagname = "TEST_COMPILED_DAG";
        String dagnameExec = "TEST_COMPILED_TOBE_DAG";
        String step = "step1";
        String group = "group.test";
        String jarname = "okCompiledTest.jar";
		String jarnameExec = "okCompiledToBeTest.jar";
        var cmd = "return \"testing\";";
		StringBuilder source = new StringBuilder("dag.isCompiled(true).setArgs(\"{}\",\"{}\").execute(\""+jarnameExec+"\",\"generated_dag.main."+dagnameExec+"\");\n");
		source.append("return \"test\";");
		var cmdFinal = source.toString();
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
        	JobsPage jobsPage = authenticatedPage.goToJobs();
            this.createGroovyJob(jobsPage, dagnameExec, step, group, jarnameExec, cmd);
            JobsUncompiledTab tab = jobsPage.goToUncompiledTab();
			tab.compileDesign(jarname);
			jobsPage = authenticatedPage.goToJobs();
        	this.createGroovyJob(jobsPage, dagname, step, group, jarname, cmdFinal);
        	jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step, jarname, dagname,jobsPage);
			if(!status.isEmpty()) {
				if(status.length() == 1) {
					var jretu = status.getJSONObject(0);
					if(jretu.get("output").equals("test")) {
						authenticatedPage.goToJobs();
	                    authenticatedPage.logout();
	        			Assertions.assertTrue(true);
					}
				} else {
        			Assertions.fail("Problema al ejecutar el operador?");
        		}
			}
        }
    }

    @Test(priority = 8)
    public void failExcepcionTest() throws InterruptedException {
		log.info("failExcepcionTest");
        String dagname = "TEST_FAIL_EXCEPTION_DAG";
        String step = "step1";
        String group = "group.test";
        String jarname = "failExcepcionTest.jar";
        var cmd1 = "return new DomainException(new Exception(\""+step+"\"));";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
        	JobsPage jobsPage = authenticatedPage.goToJobs();
        	this.createGroovyJob(jobsPage, dagname, step, group, jarname, cmd1);
        	jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step, jarname, dagname,jobsPage);
        	if(status.isEmpty()) {	
        		authenticatedPage.goToJobs();
                authenticatedPage.logout();
        		Assertions.assertTrue(true);
        	}
        }
    }
	 
    @Test(priority = 9)
    public void canBeExecutedInGroovyTest() throws InterruptedException {
		log.info("canBeExecutedInGroovyTest");
    	String dagname = "TEST_EXECUTED_BY_GROOVY_DAG";
        String step = "step1";
        String group = "group.test";
        String jarname = "canBeExecutedInGroovyTest.jar";
        String cmd1 = "def args = new Properties();def optionals = new Properties();def sourceStr = \"source\";def code = \"return \\\"test\\\"\";args.setProperty(sourceStr,code );return operator.setArgs(args).setOptionals(optionals).setOperator(\"GroovyOperator\").execute()";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
        	JobsPage jobsPage = authenticatedPage.goToJobs();
        	this.createGroovyJob(jobsPage, dagname, step, group, jarname, cmd1);
        	jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step, jarname, dagname,jobsPage);
        	if(!status.isEmpty()) {
        		if(status.getJSONObject(0).get("output").equals(step)) {
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
	
    @Test(priority = 10)
    public void sourceCanBeOutputStepTest() throws InterruptedException {
		log.info("sourceCanBeOutputStepTest");
		String dagname = "TEST_GROOVY_CAN_BE_OUTPUTED_DAG";
        String step1 = "step1";
		String step2 = "step2";
        String group = "group.test";
        String jarname = "sourceCanBeOutputStepTest.jar";
        String cmd1 = "return \"return \\\"test\\\"\"";
		String cmd2 = "${step1}";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
        	JobsPage jobsPage = authenticatedPage.goToJobs();
        	this.createGroovyJob(jobsPage, dagname, step1, group, jarname, cmd1);
        	jobsPage = authenticatedPage.goToJobs();
			
			var canvas =jobsPage.goToUncompiledTab().editDesign(jarname);
			canvas.selectDag(dagname);
			canvas.addStep(dagname,step2,"main.cl.dagserver.infra.adapters.operators.GroovyOperator");
			EditorParamModal params = canvas.selectStage(step2);
			params.selectTab("//*[@id=\"profile_li\"]/a");
			params.sendScript(cmd2);
			params.save();
			canvas.save();
			canvas.close();

			jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step2, jarname, dagname,jobsPage);
        	if(!status.isEmpty()) {
        		if(status.getJSONObject(0).get("output").equals("test")) {
        			authenticatedPage.goToJobs();
                    authenticatedPage.logout();
        			Assertions.assertTrue(true);
        		} else {
        			Assertions.fail("Problema al ejecutar el operador?");
        		}
        	}
        }
    }
}
