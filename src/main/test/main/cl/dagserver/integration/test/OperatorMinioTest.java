package main.cl.dagserver.integration.test;

import org.junit.jupiter.api.Assertions;
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
import java.io.ByteArrayInputStream;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.MakeBucketArgs;

@Log4j2
public class OperatorMinioTest extends BaseOperatorTest {

    @SuppressWarnings("rawtypes")
    private GenericContainer minioContainer;
    private String host = "host.docker.internal";
    private Integer minioPort = 9000;
    private Integer minioConsolePort = 9001;
    private String minioUser = "admin";
    private String minioPass = "admin123";

    @SuppressWarnings({ "resource", "rawtypes" })
    @BeforeMethod
    public void setUp() throws Exception {
        this.minioContainer = new GenericContainer ("quay.io/minio/minio")
        		.withExposedPorts(minioPort,minioConsolePort)
                .withEnv("MINIO_ROOT_USER", minioUser)
                .withEnv("MINIO_ROOT_PASSWORD", minioPass)
                .withCommand("server", "/data", "--console-address", ":9001");;

        this.minioContainer.start();
        Thread.sleep(10000); 
        log.info("Minio container started at {}:{}", host, minioPort);
        this.createMinioBucketWithTestFile("test-bucket");
    }

    public void createMinioBucketWithTestFile(String bucketName) throws Exception {
        MinioClient minioClient = MinioClient.builder()
                .endpoint("http://" + host + ":" + this.minioContainer.getFirstMappedPort() )
                .credentials(minioUser, minioPass)
                .build();
        // Crear el bucket si no existe
        boolean exists = minioClient.bucketExists(io.minio.BucketExistsArgs.builder().bucket(bucketName).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
        // Subir el archivo test.txt con contenido "hola mundo"
        byte[] content = "hola mundo".getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(content);
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucketName)
                .object("test.txt")
                .stream(bais, content.length, -1)
                .contentType("text/plain")
                .build()
        );
        bais.close();
    }

    @AfterMethod
    public void tearDown() {
        this.minioContainer.stop();
    }

    @Test(priority = 1)
    public void minioOperatorListTest() throws InterruptedException {
        log.info("minioOperatorListTest");
        String dagname = "TEST_MINIO1_DAG";
        String jarname = "miniotest1.jar";
        String step = "step1";
        String group = "group.test";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            createKeystore(authenticatedPage,"keystore1",minioUser,minioPass);            
            JobsPage jobsPage = authenticatedPage.goToJobs();
        	CanvasDagEditor canvas = createJob(jobsPage,jarname, dagname, group);
            canvas.addStep(dagname,step,"main.cl.dagserver.infra.adapters.operators.MinioOperator");
        	EditorParamModal params = canvas.selectStage(step);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("host", "http://" + host + ":" + this.minioContainer.getFirstMappedPort(), "input");
            params.sendParameter("baseBucket", "test-bucket", "input");
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
    public void minioOperatorDownloadTest() throws InterruptedException {
        log.info("minioOperatorDownloadTest");
        String dagname = "TEST_MINIO1_DAG";
        String jarname = "miniotest2.jar";
        String step = "step1";
        String group = "group.test";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            createKeystore(authenticatedPage,"keystore1",minioUser,minioPass);            
            JobsPage jobsPage = authenticatedPage.goToJobs();
        	CanvasDagEditor canvas = createJob(jobsPage,jarname, dagname, group);
            canvas.addStep(dagname,step,"main.cl.dagserver.infra.adapters.operators.MinioOperator");
        	EditorParamModal params = canvas.selectStage(step);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("host", "http://" + host + ":" + this.minioContainer.getFirstMappedPort(), "input");
            params.sendParameter("baseBucket", "test-bucket", "input");
    		params.sendParameter("credentials", "keystore1", "list");
    		params.selectTab("//*[@id=\"remote_li\"]/a");
    		params.sendRemote("download","/test.txt","/test.txt");
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
    public void minioOperatorUploadTest() throws InterruptedException {
        log.info("minioOperatorUploadTest");
        String dagname = "TEST_MINIO1_DAG";
        String jarname = "miniotest3.jar";
        String step = "step1";
        String group = "group.test";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            createKeystore(authenticatedPage,"keystore1",minioUser,minioPass);
            JobsPage jobsPage = authenticatedPage.goToJobs();
        	CanvasDagEditor canvas = createJob(jobsPage,jarname, dagname, group);
            canvas.addStep(dagname,step,"main.cl.dagserver.infra.adapters.operators.MinioOperator");
        	EditorParamModal params = canvas.selectStage(step);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("host", "http://" + host + ":" + this.minioContainer.getFirstMappedPort(), "input");
            params.sendParameter("baseBucket", "test-bucket", "input");
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
        String cmd1 = "def args = new Properties();def optionals = new Properties();" +
                  "args.setProperty(\"host\",\""+"http://" + host + ":" + this.minioContainer.getFirstMappedPort()+"\" );"+
                  "args.setProperty(\"credentials\",\"keystore1\" );"+
                  "args.setProperty(\"baseBucket\",\"test-bucket\" );"+
                  "args.setProperty(\"commands\",\"list /\" );"+
                  "return operator.setArgs(args).setOptionals(optionals).setOperator(\"MinioOperator\").execute()";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
        	createKeystore(authenticatedPage,"keystore1",minioUser,minioPass);
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
        String cmd1 = "return \""+"http://" + host + ":" + this.minioContainer.getFirstMappedPort()+"\"";

    	
    	LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
        	createKeystore(authenticatedPage,"keystore1",minioUser,minioPass);
        	JobsPage jobsPage = authenticatedPage.goToJobs();
        	createGroovyJob(jobsPage, dagname, step1, group, jarname, cmd1);
        	
        	jobsPage = authenticatedPage.goToJobs();
            JobsUncompiledTab uncompileds = jobsPage.goToUncompiledTab();
            uncompileds.searchUncompiled(jarname);
            CanvasDagEditor canvas = uncompileds.editDesign(jarname);
            canvas.selectDag(dagname);

            canvas.addStep(dagname,step2,"main.cl.dagserver.infra.adapters.operators.MinioOperator");
            EditorParamModal params = canvas.selectStage(step2);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("host", "${step0}", "input");
    		params.sendParameter("credentials", "keystore1", "list");
            params.sendParameter("baseBucket", "test-bucket", "input");
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

    @Test(priority = 6)
    public void bucketCanBeOutputStepTest() throws InterruptedException {
    	log.info("bucketCanBeOutputStepTest");
    	String dagname = "TEST_FILE1_DAG";
        String step1 = "step0";
        String step2 = "step1";
        String group = "group.test";
        String jarname = "filetest6.jar";
        String cmd1 = "return \"test-bucket\"";

    	
    	LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
        	createKeystore(authenticatedPage,"keystore1",minioUser,minioPass);
        	JobsPage jobsPage = authenticatedPage.goToJobs();
        	createGroovyJob(jobsPage, dagname, step1, group, jarname, cmd1);
        	
        	jobsPage = authenticatedPage.goToJobs();
            JobsUncompiledTab uncompileds = jobsPage.goToUncompiledTab();
            uncompileds.searchUncompiled(jarname);
            CanvasDagEditor canvas = uncompileds.editDesign(jarname);
            canvas.selectDag(dagname);

            canvas.addStep(dagname,step2,"main.cl.dagserver.infra.adapters.operators.MinioOperator");
            EditorParamModal params = canvas.selectStage(step2);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("host", "http://" + host + ":" + this.minioContainer.getFirstMappedPort(), "input");
    		params.sendParameter("credentials", "keystore1", "list");
            params.sendParameter("baseBucket", "${step0}", "input");
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
