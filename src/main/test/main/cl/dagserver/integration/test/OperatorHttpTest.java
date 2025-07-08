package main.cl.dagserver.integration.test;

import java.io.IOException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Assertions;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import main.cl.dagserver.integration.pom.AuthenticatedPage;
import main.cl.dagserver.integration.pom.JobsPage;
import main.cl.dagserver.integration.pom.LoginPage;
import main.cl.dagserver.integration.pom.segments.CanvasDagEditor;
import main.cl.dagserver.integration.pom.segments.EditorParamModal;
import main.cl.dagserver.integration.pom.segments.JobsUncompiledTab;
import main.cl.dagserver.integration.test.core.BaseOperatorTest;


public class OperatorHttpTest extends BaseOperatorTest {

	@SuppressWarnings("rawtypes")
	private GenericContainer webserverContainer;
	private String urlr = "http://host.docker.internal/";
	
    @SuppressWarnings({ "resource", "deprecation", "rawtypes" })
	@BeforeMethod
    public void setUp() throws InterruptedException {
        this.webserverContainer = new FixedHostPortGenericContainer("edmur/webhooks.standalone")
            .withFixedExposedPort(80,80);
        this.webserverContainer.start();
        Thread.sleep(10000);
        // 
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost("http://host.docker.internal/token");
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                String uuid = jsonNode.get("uuid").asText();
                urlr = urlr + uuid;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
    @AfterMethod
    public void tearDown() {
        this.webserverContainer.stop();
    }
    
    @Test(priority = 1)
    public void httpOperatorGetTest() throws InterruptedException {
    	String dagname = "TEST_FILE1_DAG";
        String step = "step1";
        String group = "group.test";
        String jarname = "filetest1.jar";
        String timeoutr = "10000";
    	String methodr = "GET";
    	String contentr = "text/html";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
        	JobsPage jobsPage = authenticatedPage.goToJobs();
        	CanvasDagEditor canvas = createJob(jobsPage,jarname, dagname, group);
        	canvas.addStep(dagname,step,"main.cl.dagserver.infra.adapters.operators.HttpOperator");
        	EditorParamModal params = canvas.selectStage(step);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("timeout", timeoutr, "input");
    		params.sendParameter("contentType", contentr, "input");
    		params.sendParameter("method", methodr, "list");
    		params.sendParameter("url", urlr, "input");
    		params.save();
            canvas.saveJar();
        	jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step, jarname, dagname,jobsPage);
        	if(!status.isEmpty()) {
        		Integer rc = status.getJSONObject(0).getInt("responseCode");
        		if(rc.equals(200)) {
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

    @Test(priority = 2)
    public void httpOperatorPostTest() throws InterruptedException {
    
    	String dagname = "TEST_FILE1_DAG";
        String step1 = "step0";
        String step2 = "step1";
        String group = "group.test";
        String jarname = "filetest1.jar";
        String methodr = "POST";
    	String contentr = "text/html";
    	String timeoutr = "10000";
        String cmd1 = "import org.json.JSONArray;var arr = [[key:'value']];return new JSONArray(arr);";
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

            canvas.addStep(dagname,step2,"main.cl.dagserver.infra.adapters.operators.HttpOperator");
            EditorParamModal params = canvas.selectStage(step2);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("timeout", timeoutr, "input");
    		params.sendParameter("contentType", contentr, "input");
    		params.sendParameter("method", methodr, "list");
    		params.sendParameter("xcom", step1,"list");
    		params.sendParameter("url", urlr, "input");
            params.save();
            canvas.save();
            canvas.close();
        	jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step2, jarname, dagname,jobsPage);
        	if(!status.isEmpty()) {
        		Integer rc = status.getJSONObject(0).getInt("responseCode");
        		if(rc.equals(200)) {
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

    @Test(priority = 3)
    public void httpOperatorPutTest() throws InterruptedException {
    	String dagname = "TEST_FILE1_DAG";
        String step1 = "step0";
        String step2 = "step1";
        String group = "group.test";
        String jarname = "filetest1.jar";
        String methodr = "PUT";
    	String contentr = "text/html";
    	String timeoutr = "10000";
        String cmd1 = "import org.json.JSONArray;var arr = [[key:'value']];return new JSONArray(arr);";
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

            canvas.addStep(dagname,step2,"main.cl.dagserver.infra.adapters.operators.HttpOperator");
            EditorParamModal params = canvas.selectStage(step2);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("timeout", timeoutr, "input");
    		params.sendParameter("contentType", contentr, "input");
    		params.sendParameter("method", methodr, "list");
    		params.sendParameter("xcom", step1,"list");
    		params.sendParameter("url", urlr, "input");
            params.save();
            canvas.save();
            canvas.close();
        	jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step2, jarname, dagname,jobsPage);
        	if(!status.isEmpty()) {
        		Integer rc = status.getJSONObject(0).getInt("responseCode");
        		if(rc.equals(200)) {
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

    @Test(priority = 4)
    public void httpOperatorDeleteTest() throws InterruptedException {
    	String dagname = "TEST_FILE1_DAG";
        String step1 = "step0";
        String step2 = "step1";
        String group = "group.test";
        String jarname = "filetest1.jar";
        String methodr = "DELETE";
    	String contentr = "text/html";
    	String timeoutr = "10000";
        String cmd1 = "import org.json.JSONArray;var arr = [[key:'value']];return new JSONArray(arr);";
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

            canvas.addStep(dagname,step2,"main.cl.dagserver.infra.adapters.operators.HttpOperator");
            EditorParamModal params = canvas.selectStage(step2);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("timeout", timeoutr, "input");
    		params.sendParameter("contentType", contentr, "input");
    		params.sendParameter("method", methodr, "list");
    		params.sendParameter("xcom", step1,"list");
    		params.sendParameter("url", urlr, "input");
            params.save();
            canvas.save();
            canvas.close();
        	jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step2, jarname, dagname,jobsPage);
        	if(!status.isEmpty()) {
        		Integer rc = status.getJSONObject(0).getInt("responseCode");
        		if(rc.equals(200)) {
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
    public void canBeExecutedInGroovyTest() throws InterruptedException {

        String dagname = "TEST_EXECUTED_BY_GROOVY_DAG";
        String step = "step1";
        String group = "group.test";
        String jarname = "canBeExecutedInGroovyTest.jar";
        String cmd1 = "def args = new Properties();def optionals = new Properties();" +
                  "args.setProperty(\"timeout\",\"10000\" );"+
                  "args.setProperty(\"contentType\",\"text/html\" );"+
                  "args.setProperty(\"method\",\"GET\" );"+
                  "args.setProperty(\"url\",\""+urlr+"\" );"+
                  "return operator.setArgs(args).setOptionals(optionals).setOperator(\"HttpOperator\").execute()";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
        	JobsPage jobsPage = authenticatedPage.goToJobs();
        	this.createGroovyJob(jobsPage, dagname, step, group, jarname, cmd1);
        	jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step, jarname, dagname,jobsPage);
        	if(!status.isEmpty()) {
                Integer rc = status.getJSONObject(0).getInt("responseCode");
        		if(rc.equals(200)) {
        			authenticatedPage.goToJobs();
                    authenticatedPage.logout();
        			Assertions.assertTrue(true);
        		} else {
        			Assertions.fail("Problema al ejecutar el operador?");
        		}
        	}
        }

    }
    @Test(priority = 11)
    public void urlCanBeOutputStepTest() throws InterruptedException {
    	String dagname = "TEST_FILE1_DAG";
        String step1 = "step0";
        String step2 = "step1";
        String group = "group.test";
        String jarname = "filetest1.jar";
        String methodr = "GET";
    	String contentr = "text/html";
    	String timeoutr = "10000";
        String cmd1 = "return \""+urlr+"\";";
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

            canvas.addStep(dagname,step2,"main.cl.dagserver.infra.adapters.operators.HttpOperator");
            EditorParamModal params = canvas.selectStage(step2);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("timeout", timeoutr, "input");
    		params.sendParameter("contentType", contentr, "input");
    		params.sendParameter("method", methodr, "list");
    		params.sendParameter("url", "${"+step1+"}", "input");
            params.save();
            canvas.save();
            canvas.close();
        	jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step2, jarname, dagname,jobsPage);
        	if(!status.isEmpty()) {
        		Integer rc = status.getJSONObject(0).getInt("responseCode");
        		if(rc.equals(200)) {
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
    @Test(priority = 12)
    public void authorizationHeaderCanBeOutputStepTest() throws InterruptedException {
    	String dagname = "TEST_FILE1_DAG";
        String step1 = "step0";
        String step2 = "step1";
        String group = "group.test";
        String jarname = "filetest1.jar";
        String methodr = "GET";
    	String contentr = "text/html";
    	String timeoutr = "10000";
        String cmd1 = "return \"Bearer 123ABC\";";
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

            canvas.addStep(dagname,step2,"main.cl.dagserver.infra.adapters.operators.HttpOperator");
            EditorParamModal params = canvas.selectStage(step2);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("timeout", timeoutr, "input");
    		params.sendParameter("contentType", contentr, "input");
    		params.sendParameter("method", methodr, "list");
    		params.sendParameter("url", urlr, "input");
    		params.sendParameter("authorizationHeader", "${"+step1+"}", "input");
            params.save();
            canvas.save();
            canvas.close();
        	jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step2, jarname, dagname,jobsPage);
        	if(!status.isEmpty()) {
        		Integer rc = status.getJSONObject(0).getInt("responseCode");
        		if(rc.equals(200)) {
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
