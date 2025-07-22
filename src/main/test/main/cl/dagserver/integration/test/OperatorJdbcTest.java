package main.cl.dagserver.integration.test;

import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.MySQLContainer;
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
public class OperatorJdbcTest extends BaseOperatorTest {

    @SuppressWarnings("rawtypes")
	private MySQLContainer mySQLContainer;

    @SuppressWarnings("resource")
	@BeforeMethod
    public void setUp() throws InterruptedException {
    	String hostPath = Paths.get("selenium").toAbsolutePath().toString();
        String containerPath = "/selenium";
        this.mySQLContainer = new MySQLContainer<>("mysql:8.0.30")
		        .withDatabaseName("testcontainer")
		        .withUsername("test")
		        .withPassword("test")
		        .withEnv("MYSQL_ROOT_HOST", "%")
		        .withFileSystemBind(hostPath, containerPath, BindMode.READ_WRITE)
		        .withInitScript("init.sql")
		        .withExposedPorts(3306);
		try {
			this.mySQLContainer.start();			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    @AfterMethod
    public void tearDown() {
        this.mySQLContainer.stop();
    }

    @Test(priority = 1)
    public void executeSelectJdbc() throws InterruptedException {
        log.info("executeSelectJdbc");
        String dagname = "TEST_FILE1_DAG";
        String step = "step1";
        String group = "group.test";
        String jarname = "filetest1.jar";
        String jdbcdriver = "com.mysql.cj.jdbc.Driver";
        String sql = "SELECT * FROM testcontainer.tests";
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
            canvas.addStep(dagname,step,"main.cl.dagserver.infra.adapters.operators.JdbcOperator");
            EditorParamModal params = canvas.selectStage(step);
            params.selectTab("//*[@id=\"home_li\"]/a");
            var jdbcurl = this.mySQLContainer.getJdbcUrl().replace("localhost", "host.docker.internal");
    		params.sendParameter("url", jdbcurl, "input");
            params.sendParameter("credentials", "keystore1", "list");
    		params.sendParameter("driver", jdbcdriver, "input");
    		params.selectTab("//*[@id=\"file_li\"]/a");
            params.selectFile("driverPath","//root//dags//drivers//mysql-connector-j-9.0.0.jar");
    		params.selectTab("//*[@id=\"profile_li\"]/a");
    		params.sendScript(sql);

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
        		Assertions.fail("Problema al ejecutar el operador?");
        	}
        }
    }
    @Test(priority = 2)
    public void executeSelectWParamsJdbc() throws InterruptedException {
        log.info("executeSelectWParamsJdbc");
    	String dagname = "TEST_FILE1_DAG";
    	String step1 = "step0";
    	String step2 = "step1";
        String group = "group.test";
        String jarname = "filetest2.jar";
        String jdbcdriver = "com.mysql.cj.jdbc.Driver";
        String sql = "SELECT * FROM testcontainer.tests where label = :label";
        String cmd1 = "return [[label:'test']]";
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
            canvas.addStep(dagname,step2,"main.cl.dagserver.infra.adapters.operators.JdbcOperator");
            EditorParamModal params = canvas.selectStage(step2);
            params.selectTab("//*[@id=\"home_li\"]/a");
            var jdbcurl = this.mySQLContainer.getJdbcUrl().replace("localhost", "host.docker.internal");
    		params.sendParameter("url", jdbcurl, "input");
            params.sendParameter("credentials", "keystore1", "list");
    		params.sendParameter("driver", jdbcdriver, "input");
    		params.sendParameter("xcom", step1,"list");
    		params.selectTab("//*[@id=\"file_li\"]/a");
            params.selectFile("driverPath","//root//dags//drivers//mysql-connector-j-9.0.0.jar");
    		params.selectTab("//*[@id=\"profile_li\"]/a");
    		params.sendScript(sql);

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

    @Test(priority = 3)
    public void executeInsertJdbc() throws InterruptedException {
        log.info("executeInsertJdbc");
    	String dagname = "TEST_FILE1_DAG";
    	String step1 = "step0";
    	String step2 = "step1";
        String group = "group.test";
        String jarname = "filetest3.jar";
        String jdbcdriver = "com.mysql.cj.jdbc.Driver";
        String sql = "insert into tests(label) values (:label);";
        String cmd1 = "return [[label:'inserted']]";
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
            canvas.addStep(dagname,step2,"main.cl.dagserver.infra.adapters.operators.JdbcOperator");
            EditorParamModal params = canvas.selectStage(step2);
            params.selectTab("//*[@id=\"home_li\"]/a");
            var jdbcurl = this.mySQLContainer.getJdbcUrl().replace("localhost", "host.docker.internal");
    		params.sendParameter("url", jdbcurl, "input");
            params.sendParameter("credentials", "keystore1", "list");
    		params.sendParameter("driver", jdbcdriver, "input");
    		params.sendParameter("xcom", step1,"list");
    		params.selectTab("//*[@id=\"file_li\"]/a");
            params.selectFile("driverPath","//root//dags//drivers//mysql-connector-j-9.0.0.jar");
    		params.selectTab("//*[@id=\"profile_li\"]/a");
    		params.sendScript(sql);

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

    @Test(priority = 4)
    public void executeUpdateJdbc() throws InterruptedException {
        log.info("executeUpdateJdbc");
    	String dagname = "TEST_FILE1_DAG";
    	String step1 = "step0";
    	String step2 = "step1";
        String group = "group.test";
        String jarname = "filetest4.jar";
        String jdbcdriver = "com.mysql.cj.jdbc.Driver";
        String sql = "update testcontainer.tests set label = :new_value where label = :old_value;";
        String cmd1 = "return [[old_value:'updated',new_value:'updated1']]";
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
            canvas.addStep(dagname,step2,"main.cl.dagserver.infra.adapters.operators.JdbcOperator");
            EditorParamModal params = canvas.selectStage(step2);
            params.selectTab("//*[@id=\"home_li\"]/a");
            var jdbcurl = this.mySQLContainer.getJdbcUrl().replace("localhost", "host.docker.internal");
    		params.sendParameter("url", jdbcurl, "input");
            params.sendParameter("credentials", "keystore1", "list");
    		params.sendParameter("driver", jdbcdriver, "input");
    		params.sendParameter("xcom", step1,"list");
    		params.selectTab("//*[@id=\"file_li\"]/a");
            params.selectFile("driverPath","//root//dags//drivers//mysql-connector-j-9.0.0.jar");
    		params.selectTab("//*[@id=\"profile_li\"]/a");
    		params.sendScript(sql);

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

    @Test(priority = 5)
    public void executeDeleteJdbc() throws InterruptedException {
        log.info("executeDeleteJdbc");
    	String dagname = "TEST_FILE1_DAG";
    	String step1 = "step0";
    	String step2 = "step1";
        String group = "group.test";
        String jarname = "filetest5.jar";
        String jdbcdriver = "com.mysql.cj.jdbc.Driver";
        String sql = "delete FROM testcontainer.tests where label = :label";
        String cmd1 = "return [[label:'deleteme']]";
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
            canvas.addStep(dagname,step2,"main.cl.dagserver.infra.adapters.operators.JdbcOperator");
            EditorParamModal params = canvas.selectStage(step2);
            params.selectTab("//*[@id=\"home_li\"]/a");
            var jdbcurl = this.mySQLContainer.getJdbcUrl().replace("localhost", "host.docker.internal");
    		params.sendParameter("url", jdbcurl, "input");
            params.sendParameter("credentials", "keystore1", "list");
    		params.sendParameter("driver", jdbcdriver, "input");
    		params.sendParameter("xcom", step1,"list");
    		params.selectTab("//*[@id=\"file_li\"]/a");
            params.selectFile("driverPath","//root//dags//drivers//mysql-connector-j-9.0.0.jar");
    		params.selectTab("//*[@id=\"profile_li\"]/a");
    		params.sendScript(sql);

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
    public void canBeExecutedInGroovyTest() throws InterruptedException {
        log.info("canBeExecutedInGroovyTest");
    	String dagname = "TEST_EXECUTED_BY_GROOVY_DAG";
        String step = "step1";
        String group = "group.test";
        String jdbcdriver = "com.mysql.cj.jdbc.Driver";
        String jarname = "canBeExecutedInGroovyTest.jar";
        var jdbcurl = this.mySQLContainer.getJdbcUrl().replace("localhost", "host.docker.internal");
        String cmd1 = "def args = new Properties();def optionals = new Properties();" +
                  "args.setProperty(\"url\",\""+jdbcurl+"\" );"+
                  "args.setProperty(\"credentials\",\"keystore1\" );"+
                  "args.setProperty(\"driver\",\""+jdbcdriver+"\" );"+
                  "args.setProperty(\"driverPath\",\"//root//dags//drivers//mysql-connector-j-9.0.0.jar\" );"+
                  "args.setProperty(\"query\",\"SELECT * FROM testcontainer.tests\" );"+
                  "return operator.setArgs(args).setOptionals(optionals).setOperator(\"JdbcOperator\").execute()";
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
        	}
        }
    }
    @Test(priority = 7)
    public void urlCanBeOutputStepTest() throws InterruptedException {
        log.info("urlCanBeOutputStepTest");
        String dagname = "TEST_FILE1_DAG";
    	String step1 = "step0";
    	String step2 = "step1";
        String group = "group.test";
        String jarname = "filetest7.jar";
        String jdbcdriver = "com.mysql.cj.jdbc.Driver";
        String sql = "SELECT * FROM testcontainer.tests";
        var jdbcurl = this.mySQLContainer.getJdbcUrl().replace("localhost", "host.docker.internal");
        String cmd1 = "return \""+jdbcurl+"\"";
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
            canvas.addStep(dagname,step2,"main.cl.dagserver.infra.adapters.operators.JdbcOperator");
            EditorParamModal params = canvas.selectStage(step2);
            params.selectTab("//*[@id=\"home_li\"]/a");
            
    		params.sendParameter("url", "${step0}", "input");
            params.sendParameter("credentials", "keystore1", "list");
    		params.sendParameter("driver", jdbcdriver, "input");
    		params.selectTab("//*[@id=\"file_li\"]/a");
            params.selectFile("driverPath","//root//dags//drivers//mysql-connector-j-9.0.0.jar");
    		params.selectTab("//*[@id=\"profile_li\"]/a");
    		params.sendScript(sql);

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
    @Test(priority = 8)
    public void driverCanBeOutputStepTest() throws InterruptedException {
        log.info("driverCanBeOutputStepTest");
    	String dagname = "TEST_FILE1_DAG";
    	String step1 = "step0";
    	String step2 = "step1";
        String group = "group.test";
        String jarname = "filetest8.jar";
        String jdbcdriver = "com.mysql.cj.jdbc.Driver";
        String sql = "SELECT * FROM testcontainer.tests";
        var jdbcurl = this.mySQLContainer.getJdbcUrl().replace("localhost", "host.docker.internal");
        String cmd1 = "return \""+jdbcdriver+"\"";
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
            canvas.addStep(dagname,step2,"main.cl.dagserver.infra.adapters.operators.JdbcOperator");
            EditorParamModal params = canvas.selectStage(step2);
            params.selectTab("//*[@id=\"home_li\"]/a");
            
    		params.sendParameter("url", jdbcurl, "input");
            params.sendParameter("credentials", "keystore1", "list");
    		params.sendParameter("driver", "${step0}", "input");
    		params.selectTab("//*[@id=\"file_li\"]/a");
            params.selectFile("driverPath","//root//dags//drivers//mysql-connector-j-9.0.0.jar");
    		params.selectTab("//*[@id=\"profile_li\"]/a");
    		params.sendScript(sql);

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
    @Test(priority = 9)
    public void driverPathCanBeOutputStepTest() throws InterruptedException {
        log.info("driverPathCanBeOutputStepTest");
    	String dagname = "TEST_FILE1_DAG";
    	String step1 = "step0";
    	String step2 = "step1";
        String group = "group.test";
        String jarname = "filetest9.jar";
        String jdbcdriver = "com.mysql.cj.jdbc.Driver";
        String sql = "SELECT * FROM testcontainer.tests";
        var jdbcurl = this.mySQLContainer.getJdbcUrl().replace("localhost", "host.docker.internal");
        String cmd1 = "return \"//root//dags//drivers//mysql-connector-j-9.0.0.jar\"";
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
            canvas.addStep(dagname,step2,"main.cl.dagserver.infra.adapters.operators.JdbcOperator");
            EditorParamModal params = canvas.selectStage(step2);
            params.selectTab("//*[@id=\"home_li\"]/a");
            
    		params.sendParameter("url", jdbcurl, "input");
            params.sendParameter("credentials", "keystore1", "list");
    		params.sendParameter("driver", jdbcdriver, "input");
    		params.selectTab("//*[@id=\"file_li\"]/a");
            params.selectFile("driverPath","${step0}");
    		params.selectTab("//*[@id=\"profile_li\"]/a");
    		params.sendScript(sql);

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
    @Test(priority = 10)
    public void queryCanBeOutputStepTest() throws InterruptedException {
        log.info("queryCanBeOutputStepTest");
    	String dagname = "TEST_FILE1_DAG";
    	String step1 = "step0";
    	String step2 = "step1";
        String group = "group.test";
        String jarname = "filetest10.jar";
        String jdbcdriver = "com.mysql.cj.jdbc.Driver";
        String sql = "SELECT * FROM testcontainer.tests";
        var jdbcurl = this.mySQLContainer.getJdbcUrl().replace("localhost", "host.docker.internal");
        String cmd1 = "return \""+sql+"\"";
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
            canvas.addStep(dagname,step2,"main.cl.dagserver.infra.adapters.operators.JdbcOperator");
            EditorParamModal params = canvas.selectStage(step2);
            params.selectTab("//*[@id=\"home_li\"]/a");
            
    		params.sendParameter("url", jdbcurl, "input");
            params.sendParameter("credentials", "keystore1", "list");
    		params.sendParameter("driver", jdbcdriver, "input");
    		params.selectTab("//*[@id=\"file_li\"]/a");
            params.selectFile("driverPath","//root//dags//drivers//mysql-connector-j-9.0.0.jar");
    		params.selectTab("//*[@id=\"profile_li\"]/a");
    		params.sendScript("${step0}");

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
