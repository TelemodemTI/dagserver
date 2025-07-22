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
@SuppressWarnings("rawtypes")
public class OperatorMailTest extends BaseOperatorTest{
	
	private GenericContainer mailsContainer;
	private String host = "host.docker.internal";
	private Integer webPort = 8025;
	private Integer port = 587;
	private String fromMail = "test@test.com";
	private String toMail = "target@test.com";
	@SuppressWarnings({ "resource", "deprecation" })
	@BeforeMethod
    public void setUp() throws InterruptedException {
		this.mailsContainer = new FixedHostPortGenericContainer("42bv/mailhog")
				.withFixedExposedPort(webPort,webPort)
				.withFixedExposedPort(port,port);
		this.mailsContainer.start();
	}
	
	@AfterMethod
    public void tearDown() {
        this.mailsContainer.stop();
    }
	
	@Test(priority = 1)
    public void bodyAsPlaintext() throws InterruptedException {
		log.info("bodyAsPlaintext");
		String dagname = "TEST_FILE1_DAG";
        String step = "step1";
        String group = "group.test";
        String jarname = "filetest1.jar";
        String subject = "subject";
    	String protocol="plaintext";
    	String body = "test body";
		LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
        	createKeystore(authenticatedPage, "keystore1", "test", "test");
        	JobsPage jobsPage = authenticatedPage.goToJobs();
        	CanvasDagEditor canvas = createJob(jobsPage,jarname, dagname, group);
            canvas.saveJar();
            

            jobsPage = authenticatedPage.goToJobs();
            JobsUncompiledTab uncompileds = jobsPage.goToUncompiledTab();
            uncompileds.searchUncompiled(jarname);
            canvas = uncompileds.editDesign(jarname);
            canvas.selectDag(dagname);

            canvas.addStep(dagname,step,"main.cl.dagserver.infra.adapters.operators.MailOperator");
            EditorParamModal params = canvas.selectStage(step);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("host", host, "input");
    		params.sendParameter("port", port.toString(),"input");
    		params.sendParameter("credentials", "keystore1","list");
    		params.sendParameter("fromMail", fromMail, "input");
    		params.sendParameter("toEmail", toMail,"input" );
    		params.sendParameter("subject", subject,"input");
    		params.sendParameter("protocol", protocol, "list");
    		
    		params.selectTab("//*[@id=\"profile_li\"]/a");
    		params.sendScript(body);
    		params.save();
    		canvas.save();
    		canvas.close();
    		
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
    public void groovyBodyAsPlaintext() throws InterruptedException {
		log.info("groovyBodyAsPlaintext");
		String dagname = "TEST_FILE1_DAG";
        String step1 = "step0";
        String step2 = "step1";
        String group = "group.test";
        String jarname = "filetest2.jar";
        String subject = "subject";
    	String protocol="plaintext";
    	String cmd1 = "return 'body generado para el test desde groovy'";
		LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
        	createKeystore(authenticatedPage, "keystore1", "test", "test");
        	JobsPage jobsPage = authenticatedPage.goToJobs();
        	createGroovyJob(jobsPage, dagname, step1, group, jarname, cmd1);
        	
            jobsPage = authenticatedPage.goToJobs();
            JobsUncompiledTab uncompileds = jobsPage.goToUncompiledTab();
            uncompileds.searchUncompiled(jarname);
            CanvasDagEditor canvas = uncompileds.editDesign(jarname);
            canvas.selectDag(dagname);

            canvas.addStep(dagname,step2,"main.cl.dagserver.infra.adapters.operators.MailOperator");
            EditorParamModal params = canvas.selectStage(step2);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("host", host, "input");
    		params.sendParameter("port", port.toString(),"input");
    		params.sendParameter("credentials", "keystore1","list");
    		params.sendParameter("fromMail", fromMail, "input");
    		params.sendParameter("toEmail", toMail,"input" );
    		params.sendParameter("subject", subject,"input");
    		params.sendParameter("protocol", protocol, "list");
    		params.sendParameter("xcom", step1,"list");
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
        		Assertions.fail("no se ejecuto el operador");
        	}
        }	
	}
	
	@Test(priority = 3)
    public void bodyAstLSV12() throws InterruptedException {
		log.info("bodyAstLSV12");
		String dagname = "TEST_FILE1_DAG";
        String step = "step1";
        String group = "group.test";
        String jarname = "filetest3.jar";
        String subject = "subject";
    	String protocol="TLSv1.2";
    	String body = "test body";
		LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
        	String username = System.getenv("DAGSERVER_TEST_MAIL_USER");
        	String pwd = System.getenv("DAGSERVER_TEST_MAIL_PWD");
        	createKeystore(authenticatedPage, "keystore1", username, pwd);
        	JobsPage jobsPage = authenticatedPage.goToJobs();
        	CanvasDagEditor canvas = createJob(jobsPage,jarname, dagname, group);
            canvas.saveJar();
            

            jobsPage = authenticatedPage.goToJobs();
            JobsUncompiledTab uncompileds = jobsPage.goToUncompiledTab();
            uncompileds.searchUncompiled(jarname);
            canvas = uncompileds.editDesign(jarname);
            canvas.selectDag(dagname);

            canvas.addStep(dagname,step,"main.cl.dagserver.infra.adapters.operators.MailOperator");
            EditorParamModal params = canvas.selectStage(step);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("host", "email-smtp.us-west-2.amazonaws.com", "input");
    		params.sendParameter("port", "2587","input");
    		params.sendParameter("credentials", "keystore1","list");
    		params.sendParameter("fromMail", "contacto@telemodem.cl", "input");
    		params.sendParameter("toEmail", "gciego@gmail.com","input" );
    		params.sendParameter("subject", subject,"input");
    		params.sendParameter("protocol", protocol, "list");
    		
    		params.selectTab("//*[@id=\"profile_li\"]/a");
    		params.sendScript(body);
    		params.save();
    		canvas.save();
    		canvas.close();
    		
    		jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step, jarname, dagname,jobsPage);
        	if(!status.isEmpty()) {
        		authenticatedPage.goToJobs();
                authenticatedPage.logout();
    			Assertions.assertTrue(true);
        	}else {
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
        String jarname = "canBeExecutedInGroovyTest.jar";String subject = "subject";
    	String protocol="plaintext";
        String cmd1 = "def args = new Properties();def optionals = new Properties();" +
                  "args.setProperty(\"host\",\""+host+"\" );"+
                  "args.setProperty(\"port\",\""+port.toString()+"\" );"+
                  "args.setProperty(\"credentials\",\"keystore1\" );"+
                  "args.setProperty(\"fromMail\",\""+fromMail+"\" );"+
                  "args.setProperty(\"toEmail\",\""+toMail+"\" );"+
                  "args.setProperty(\"subject\",\""+subject+"\" );"+
                  "args.setProperty(\"protocol\",\""+protocol+"\" );"+
                  "return operator.setArgs(args).setOptionals(optionals).setOperator(\"MailOperator\").execute()";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
        	createKeystore(authenticatedPage, "keystore1", "test", "test");
        	JobsPage jobsPage = authenticatedPage.goToJobs();
        	this.createGroovyJob(jobsPage, dagname, step, group, jarname, cmd1);
        	jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step, jarname, dagname,jobsPage);
        	if(!status.isEmpty()) {
        		authenticatedPage.goToJobs();
                authenticatedPage.logout();
        		Assertions.assertTrue(true);
        	}else {
        		Assertions.fail("no se ejecuto el operador");
        	}
        }
    }
}
