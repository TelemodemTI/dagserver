package main.cl.dagserver.integration.test;

import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import main.cl.dagserver.integration.pom.AuthenticatedPage;
import main.cl.dagserver.integration.pom.JobsPage;
import main.cl.dagserver.integration.pom.LoginPage;
import main.cl.dagserver.integration.pom.segments.CanvasDagEditor;
import main.cl.dagserver.integration.pom.segments.EditorParamModal;
import main.cl.dagserver.integration.pom.segments.JobsUncompiledTab;
import main.cl.dagserver.integration.test.core.BaseOperatorTest;
import lombok.extern.log4j.Log4j2;

@Log4j2 
public class OperatorSftpTest extends BaseOperatorTest {

	@SuppressWarnings("rawtypes")
	private GenericContainer ftpContainer;
	private String host = "host.docker.internal";
	private Integer port = 22;
	private String ftpUser = "test";
	private String ftpPass = "test";
	
	@SuppressWarnings({ "resource", "unchecked", "rawtypes" })
	@BeforeMethod
    public void setUp() throws InterruptedException {
		String hostPath = Paths.get("selenium").toAbsolutePath().toString();
        String containerPath = "/home/"+ftpUser;
		this.ftpContainer = new GenericContainer(
                new ImageFromDockerfile()
                        .withDockerfileFromBuilder(builder ->
                                builder
                                        .from("atmoz/sftp:latest")
                                        .build()))
                .withExposedPorts(port)
                .withCommand(ftpUser + ":" + ftpPass + ":1001:::upload")
				.withFileSystemBind(hostPath, containerPath, BindMode.READ_WRITE);
		this.ftpContainer.start();
		Thread.sleep(1000);
	}
	@AfterMethod
    public void tearDown() {
		this.ftpContainer.stop();
	}
	
    @Test(priority = 1)
    public void sftpOperatorListTest() throws InterruptedException {
        log.info("sftpOperatorListTest");
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
            canvas.addStep(dagname,step,"main.cl.dagserver.infra.adapters.operators.SFTPOperator");
        	EditorParamModal params = canvas.selectStage(step);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("host", host, "input");
            var port1 = this.ftpContainer.getFirstMappedPort();
    		params.sendParameter("port", port1.toString(), "input");
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
    public void sftpOperatorDownloadTest() throws InterruptedException {
        log.info("sftpOperatorDownloadTest");
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
            canvas.addStep(dagname,step,"main.cl.dagserver.infra.adapters.operators.SFTPOperator");
        	EditorParamModal params = canvas.selectStage(step);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("host", host, "input");
            var port1 = this.ftpContainer.getFirstMappedPort();
    		params.sendParameter("port", port1.toString(), "input");
    		params.sendParameter("credentials", "keystore1", "list");
    		params.selectTab("//*[@id=\"remote_li\"]/a");
    		params.sendRemote("download","init.sql","init.sql");
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
    public void sftpOperatorUploadTest() throws InterruptedException {
        log.info("sftpOperatorUploadTest");
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
            canvas.addStep(dagname,step,"main.cl.dagserver.infra.adapters.operators.SFTPOperator");
        	EditorParamModal params = canvas.selectStage(step);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("host", host, "input");
            var port1 = this.ftpContainer.getFirstMappedPort();
    		params.sendParameter("port", port1.toString(), "input");
    		params.sendParameter("credentials", "keystore1", "list");
    		params.selectTab("//*[@id=\"remote_li\"]/a");
    		params.sendRemote("upload","/prueba.csv","/prueba.csv");
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
        var port1 = this.ftpContainer.getFirstMappedPort();
        String cmd1 = "def args = new Properties();def optionals = new Properties();" +
                  "args.setProperty(\"host\",\""+host+"\" );"+
                  "args.setProperty(\"port\",\""+port1.toString()+"\" );"+
                  "args.setProperty(\"credentials\",\"keystore1\" );"+
                  "args.setProperty(\"commands\",\"list /\" );"+
                  "return operator.setArgs(args).setOptionals(optionals).setOperator(\"SFTPOperator\").execute()";
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

            canvas.addStep(dagname,step2,"main.cl.dagserver.infra.adapters.operators.SFTPOperator");
            EditorParamModal params = canvas.selectStage(step2);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("host", "${step0}", "input");
            var port1 = this.ftpContainer.getFirstMappedPort();
    		params.sendParameter("port", port1.toString(), "input");
    		params.sendParameter("credentials", "keystore1", "list");
    		params.selectTab("//*[@id=\"remote_li\"]/a");
    		params.sendRemote("upload","/prueba.csv","/prueba.csv");
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
