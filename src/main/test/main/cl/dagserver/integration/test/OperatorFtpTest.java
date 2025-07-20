package main.cl.dagserver.integration.test;

import org.junit.jupiter.api.Assertions;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
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
public class OperatorFtpTest extends BaseOperatorTest {

    @SuppressWarnings("rawtypes")
	private GenericContainer ftpContainer;
	private String host = "host.docker.internal";
	private Integer port = 21;
	private Integer passivePort = 21000;
	private String ftpUser = "test";
	private String ftpPass = "test";


    @SuppressWarnings({ "resource", "deprecation", "rawtypes" })
	@BeforeMethod
    public void setUp() {
        this.ftpContainer = new FixedHostPortGenericContainer("delfer/alpine-ftp-server")
					.withFixedExposedPort(passivePort,passivePort)
					.withFixedExposedPort(port,port)		
					.withEnv("USERS", ftpUser+"|"+ftpPass)
					.withEnv("MIN_PORT", passivePort.toString())
					.withEnv("MAX_PORT", passivePort.toString());
		this.ftpContainer.start();
    }

    @AfterMethod
    public void tearDown() {
        this.ftpContainer.stop();
    }

    @Test(priority = 1)
    public void ftpOperatorListTest() throws InterruptedException {
        log.info("ftpOperatorListTest");
        String dagname = "TEST_FTP1_DAG";
        String jarname = "ftptest1.jar";
        String step = "step1";
        String group = "group.test";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            createKeystore(authenticatedPage,"keystore1",ftpUser,ftpPass);            
            JobsPage jobsPage = authenticatedPage.goToJobs();
        	CanvasDagEditor canvas = createJob(jobsPage,jarname, dagname, group);
            canvas.addStep(dagname,step,"main.cl.dagserver.infra.adapters.operators.FTPOperator");
        	EditorParamModal params = canvas.selectStage(step);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("host", host, "input");
    		params.sendParameter("port", port.toString(), "input");
    		params.sendParameter("credentials", "keystore1", "list");
    		params.selectTab("//*[@id=\"remote_li\"]/a");
    		params.sendRemote("list","/",null);
    		params.save();
    		canvas.saveJar();
            jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step, jarname, dagname,jobsPage);
        	if(!status.isEmpty()) {
                authenticatedPage.goToJobs();
                authenticatedPage.logout();
        		Assertions.assertTrue(true);
            } else {
            	Assertions.fail("no se ejecuto el operador");
            }
        }
    }

    @Test(priority = 2)
    public void ftpOperatorDownloadTest() throws InterruptedException {
        log.info("ftpOperatorDownloadTest");
        String dagname = "TEST_FTP1_DAG";
        String jarname = "ftptest2.jar";
        String step = "step1";
        String group = "group.test";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            createKeystore(authenticatedPage,"keystore1",ftpUser,ftpPass);            
            JobsPage jobsPage = authenticatedPage.goToJobs();
        	CanvasDagEditor canvas = createJob(jobsPage,jarname, dagname, group);
            canvas.addStep(dagname,step,"main.cl.dagserver.infra.adapters.operators.FTPOperator");
        	EditorParamModal params = canvas.selectStage(step);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("host", host, "input");
    		params.sendParameter("port", port.toString(), "input");
    		params.sendParameter("credentials", "keystore1", "list");
    		params.selectTab("//*[@id=\"remote_li\"]/a");
    		params.sendRemote("download","/etc/alpine-release","/alpine-release");
    		params.save();
    		canvas.saveJar();
            jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step, jarname, dagname,jobsPage);
        	if(!status.isEmpty()) {
                authenticatedPage.goToJobs();
                authenticatedPage.logout();
        		Assertions.assertTrue(true);
            } else {
            	Assertions.fail("no se ejecuto el operador");
            }
        }
    }

    @Test(priority = 3)
    public void ftpOperatorUploadTest() throws InterruptedException {
        log.info("ftpOperatorUploadTest");
        String dagname = "TEST_FTP1_DAG";
        String jarname = "ftptest3.jar";
        String step = "step1";
        String group = "group.test";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            createKeystore(authenticatedPage,"keystore1",ftpUser,ftpPass);            
            JobsPage jobsPage = authenticatedPage.goToJobs();
        	CanvasDagEditor canvas = createJob(jobsPage,jarname, dagname, group);
            canvas.addStep(dagname,step,"main.cl.dagserver.infra.adapters.operators.FTPOperator");
        	EditorParamModal params = canvas.selectStage(step);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("host", host, "input");
    		params.sendParameter("port", port.toString(), "input");
    		params.sendParameter("credentials", "keystore1", "list");
    		params.selectTab("//*[@id=\"remote_li\"]/a");
    		params.sendRemote("upload","/etc/","/prueba.csv");
    		params.save();
    		canvas.saveJar();
            jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step, jarname, dagname,jobsPage);
        	if(!status.isEmpty()) {
                authenticatedPage.goToJobs();
                authenticatedPage.logout();
        		Assertions.assertTrue(true);
            } else {
            	Assertions.fail("no se ejecuto el operador");
            }
        }
    }
    @Test(priority = 4)
    public void canBeExecutedInGroovyTest() throws InterruptedException {
        log.info("canBeExecutedInGroovyTest");
    	String dagname = "TEST_EXECUTED_BY_GROOVY_DAG";
        String step = "step1";
        String group = "group.test";
        String jarname = "canBeExecutedInGroovyTest.jar";
        String cmd1 = "def args = new Properties();def optionals = new Properties();" +
                  "args.setProperty(\"host\",\""+host+"\" );"+
                  "args.setProperty(\"port\",\""+port.toString()+"\" );"+
                  "args.setProperty(\"credentials\",\"keystore1\" );"+
                  "args.setProperty(\"commands\",\"list /\" );"+
                  "return operator.setArgs(args).setOptionals(optionals).setOperator(\"FTPOperator\").execute()";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
        	createKeystore(authenticatedPage,"keystore1",ftpUser,ftpPass);
        	JobsPage jobsPage = authenticatedPage.goToJobs();
        	this.createGroovyJob(jobsPage, dagname, step, group, jarname, cmd1);
        	jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step, jarname, dagname,jobsPage);
        	if(!status.isEmpty()) {
        		authenticatedPage.goToJobs();
                authenticatedPage.logout();
        		Assertions.assertTrue(true);
        	} else {
        		Assertions.fail("no se ejecuto el operador??");
        	}
        }
    }
    @Test(priority = 5)
    public void hostCanBeOutputStepTest() throws InterruptedException {
    	log.info("hostCanBeOutputStepTest");
    	String dagname = "TEST_FILE1_DAG";
        String step1 = "step0";
        String step2 = "step1";
        String group = "group.test";
        String jarname = "filetest5.jar";
        String cmd1 = "return \""+host+"\"";

    	
    	LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
        	createKeystore(authenticatedPage,"keystore1",ftpUser,ftpPass);
        	JobsPage jobsPage = authenticatedPage.goToJobs();
        	createGroovyJob(jobsPage, dagname, step1, group, jarname, cmd1);
        	
        	jobsPage = authenticatedPage.goToJobs();
            JobsUncompiledTab uncompileds = jobsPage.goToUncompiledTab();
            uncompileds.searchUncompiled(jarname);
            CanvasDagEditor canvas = uncompileds.editDesign(jarname);
            canvas.selectDag(dagname);

            canvas.addStep(dagname,step2,"main.cl.dagserver.infra.adapters.operators.FTPOperator");
            EditorParamModal params = canvas.selectStage(step2);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("host", "${step0}", "input");
    		params.sendParameter("port", port.toString(), "input");
    		params.sendParameter("credentials", "keystore1", "list");
    		params.selectTab("//*[@id=\"remote_li\"]/a");
    		params.sendRemote("upload","/etc/","/prueba.csv");
    		params.save();
    		canvas.save();
            canvas.close();
        	jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step2, jarname, dagname,jobsPage);
        	if(!status.isEmpty()) {
        		authenticatedPage.goToJobs();
                authenticatedPage.logout();
        		Assertions.assertTrue(true);
        	} else {
        		Assertions.fail("Problema al ejecutar el operador?");
        	}
        }

    	
    }
}
