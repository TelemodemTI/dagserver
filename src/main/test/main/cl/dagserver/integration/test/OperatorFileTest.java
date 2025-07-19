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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Log4j2
public class OperatorFileTest extends BaseOperatorTest {

    @Test(priority = 1)
    public void readFileWTitleWoRDelimiterOperator() throws InterruptedException {
        log.info("readFileWTitleWoRDelimiterOperator");
    	String dagname = "TEST_FILE1_DAG";
        String step = "step1";
        String group = "group.test";
        String jarname = "filetest1.jar";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
        	JobsPage jobsPage = authenticatedPage.goToJobs();
        	CanvasDagEditor canvas = createJob(jobsPage,jarname, dagname, group);
            canvas.addStep(dagname,step,"main.cl.dagserver.infra.adapters.operators.FileOperator");
            EditorParamModal params = canvas.selectStage(step);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("mode", "read", "list");
            params.sendParameter("firstRowTitles", "true", "list");
            params.selectTab("//*[@id=\"file_li\"]/a");
            params.selectFile("filepath","/prueba.csv");
            params.save();
            canvas.saveJar();
        	jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step, jarname, dagname,jobsPage);
        	if(!status.isEmpty()) {
        		if(status.getJSONObject(0).getString("content").contains(";")) {
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
    public void readFileWoTitleWoRDelimiterOperator() throws InterruptedException {
        log.info("readFileWoTitleWoRDelimiterOperator");
    	String dagname = "TEST_FILE1_DAG";
        String step = "step1";
        String group = "group.test";
        String jarname = "filetest1.jar";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
        	JobsPage jobsPage = authenticatedPage.goToJobs();
        	CanvasDagEditor canvas = createJob(jobsPage,jarname, dagname, group);
            canvas.addStep(dagname,step,"main.cl.dagserver.infra.adapters.operators.FileOperator");
            EditorParamModal params = canvas.selectStage(step);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("mode", "read", "list");
            params.sendParameter("firstRowTitles", "true", "list");
            params.selectTab("//*[@id=\"file_li\"]/a");
            params.selectFile("filepath","/prueba.csv");
            params.save();
            canvas.saveJar();
        	jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step, jarname, dagname,jobsPage);
        	if(!status.isEmpty()) {
        		if(status.getJSONObject(0).getString("content").contains(";")) {
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
    public void readFileWTitleWRDelimiterOperator()  throws InterruptedException {
        log.info("readFileWTitleWRDelimiterOperator");
    	String dagname = "TEST_FILE3_DAG";
        String step = "step1";
        String group = "group.test";
        String jarname = "filetest3.jar";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
        	JobsPage jobsPage = authenticatedPage.goToJobs();
        	CanvasDagEditor canvas = createJob(jobsPage,jarname, dagname, group);
            canvas.addStep(dagname,step,"main.cl.dagserver.infra.adapters.operators.FileOperator");
            EditorParamModal params = canvas.selectStage(step);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("mode", "read", "list");
            params.sendParameter("firstRowTitles", "true", "list");
            params.sendParameter("rowDelimiter", ";", "input");
            params.selectTab("//*[@id=\"file_li\"]/a");
            params.selectFile("filepath","/prueba.csv");
            params.save();
            canvas.saveJar();
        	jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step, jarname, dagname,jobsPage);
        	if(!status.isEmpty()) {
        		if(status.getJSONObject(0).get("prueba").equals("valor1")) {
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
    public void readFileWoTitleWRDelimiterOperator() throws InterruptedException {
        log.info("readFileWoTitleWRDelimiterOperator");
    	String dagname = "TEST_FILE3_DAG";
        String step = "step1";
        String group = "group.test";
        String jarname = "filetest3.jar";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
        	JobsPage jobsPage = authenticatedPage.goToJobs();
        	CanvasDagEditor canvas = createJob(jobsPage,jarname, dagname, group);
            canvas.addStep(dagname,step,"main.cl.dagserver.infra.adapters.operators.FileOperator");
            EditorParamModal params = canvas.selectStage(step);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("mode", "read", "list");
            params.sendParameter("firstRowTitles", "false", "list");
            params.sendParameter("rowDelimiter", ";", "input");
            params.selectTab("//*[@id=\"file_li\"]/a");
            params.selectFile("filepath","/prueba.csv");
            params.save();
            canvas.saveJar();
        	jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step, jarname, dagname,jobsPage);
        	if(!status.isEmpty()) {
        		var stobj = status.getJSONObject(0);
        		if(stobj.length() > 0) {
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
    @Test(priority = 5)
    public void readFileWoFileOperator() throws InterruptedException {
        log.info("readFileWoFileOperator");
    	String dagname = "TEST_FILE1_DAG";
        String step = "step1";
        String group = "group.test";
        String jarname = "filetest1.jar";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
        	JobsPage jobsPage = authenticatedPage.goToJobs();
        	CanvasDagEditor canvas = createJob(jobsPage,jarname, dagname, group);
            canvas.addStep(dagname,step,"main.cl.dagserver.infra.adapters.operators.FileOperator");
            EditorParamModal params = canvas.selectStage(step);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("mode", "read", "list");
            params.sendParameter("firstRowTitles", "true", "list");
            params.selectTab("//*[@id=\"file_li\"]/a");
            params.selectFile("filepath","/noexiste.csv");
            params.save();
            canvas.saveJar();
        	jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step, jarname, dagname,jobsPage);
        	if(status.isEmpty()) {
        		authenticatedPage.goToJobs();
                authenticatedPage.logout();
    			Assertions.assertTrue(true);
        	}
        }
    }
    @Test(priority = 6)
    public void writeFileWTitleWoRDelimiterOperator() throws InterruptedException, IOException {
        log.info("writeFileWTitleWoRDelimiterOperator");
    	String dagname = "TEST_FILE_WRITE_DAG";
    	String step1 = "step0";
    	String step2 = "step1";
        String group = "group.test";
        String jarname = "writeFileWTitleWoRDelimiterOperator.jar";
        String cmd1 = "return \"testing\"";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
        	JobsPage jobsPage = authenticatedPage.goToJobs();
        	CanvasDagEditor canvas = createJob(jobsPage,jarname, dagname, group);
        	canvas.addStep(dagname,step1,"main.cl.dagserver.infra.adapters.operators.GroovyOperator");
        	EditorParamModal params = canvas.selectStage(step1);
        	params.selectTab("//*[@id=\"profile_li\"]/a");
            params.sendScript(cmd1);
            params.save();
            canvas.saveJar();
            
            jobsPage = authenticatedPage.goToJobs();
            JobsUncompiledTab uncompileds = jobsPage.goToUncompiledTab();
            uncompileds.searchUncompiled(jarname);
            canvas = uncompileds.editDesign(jarname);
            canvas.selectDag(dagname);
            
        	canvas.addStep(dagname,step2,"main.cl.dagserver.infra.adapters.operators.FileOperator");
            params = canvas.selectStage(step2);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("mode", "write", "list");
            params.sendParameter("firstRowTitles", "true", "list");
            params.sendParameter("xcom", step1,"list");
            params.selectTab("//*[@id=\"file_li\"]/a");
            params.selectFile("filepath","/selenium/salida.txt");
            params.save();
            canvas.save();
            canvas.close();
        	jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step2, jarname, dagname,jobsPage);
        	if(status.isEmpty()) {
        		String hostPath = Paths.get("selenium/salida.txt").toAbsolutePath().toString();
                String content = new String(Files.readAllBytes(Paths.get(hostPath)));
                if("outputtesting".equals(content.trim())) {
                    // Eliminar el archivo después de verificar su contenido
                    Files.deleteIfExists(Paths.get(hostPath));
                    
                    authenticatedPage.goToJobs();
                    authenticatedPage.logout();
                    Assertions.assertTrue(true);
                } else {
                    Assertions.fail("El contenido del archivo no coincide. Esperado: 'testing', Obtenido: '" + content + "'");
                }
        	} else {        
        		Assertions.fail("Problema al ejecutar el operador?");
        	}
        }
    }
    @Test(priority = 7)
    public void writeFileWoTitleWoRDelimiterOperator() throws InterruptedException, IOException {
        log.info("writeFileWoTitleWoRDelimiterOperator");
    	String dagname = "TEST_FILE_WRITE_DAG";
    	String step1 = "step0";
    	String step2 = "step1";
        String group = "group.test";
        String jarname = "writeFileWTitleWoRDelimiterOperator.jar";
        String cmd1 = "return \"testing\"";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
        	JobsPage jobsPage = authenticatedPage.goToJobs();
        	CanvasDagEditor canvas = createJob(jobsPage,jarname, dagname, group);
        	canvas.addStep(dagname,step1,"main.cl.dagserver.infra.adapters.operators.GroovyOperator");
        	EditorParamModal params = canvas.selectStage(step1);
        	params.selectTab("//*[@id=\"profile_li\"]/a");
            params.sendScript(cmd1);
            params.save();
            canvas.saveJar();
            
            jobsPage = authenticatedPage.goToJobs();
            JobsUncompiledTab uncompileds = jobsPage.goToUncompiledTab();
            uncompileds.searchUncompiled(jarname);
            canvas = uncompileds.editDesign(jarname);
            canvas.selectDag(dagname);
            
        	canvas.addStep(dagname,step2,"main.cl.dagserver.infra.adapters.operators.FileOperator");
            params = canvas.selectStage(step2);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("mode", "write", "list");
            params.sendParameter("firstRowTitles", "false", "list");
            params.sendParameter("xcom", step1,"list");
            params.selectTab("//*[@id=\"file_li\"]/a");
            params.selectFile("filepath","/selenium/salida.txt");
            params.save();
            canvas.save();
            canvas.close();
        	jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step2, jarname, dagname,jobsPage);
        	if(status.isEmpty()) {
                String hostPath = Paths.get("selenium/salida.txt").toAbsolutePath().toString();
                String content = new String(Files.readAllBytes(Paths.get(hostPath)));
                if("testing".equals(content.trim())) {
                    // Eliminar el archivo después de verificar su contenido
                    Files.deleteIfExists(Paths.get(hostPath));
                    
                    authenticatedPage.goToJobs();
                    authenticatedPage.logout();
                    Assertions.assertTrue(true); 
                } else {
                    Assertions.fail("El contenido del archivo no coincide. Esperado: 'testing', Obtenido: '" + content + "'");
                }
        	} else {        
        		Assertions.fail("Problema al ejecutar el operador?");
        	}
        }
    }
    @Test(priority = 8)
    public void writeFileWTitleWRDelimiterOperator() throws InterruptedException, IOException {
        log.info("writeFileWTitleWRDelimiterOperator");
    	String dagname = "TEST_FILE_WRITE_DAG";
    	String step1 = "step0";
    	String step2 = "step1";
        String group = "group.test";
        String jarname = "writeFileWTitleWoRDelimiterOperator.jar";
        String cmd1 = "return \"testing1\"";
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
            
        	canvas.addStep(dagname,step2,"main.cl.dagserver.infra.adapters.operators.FileOperator");
        	EditorParamModal params = canvas.selectStage(step2);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("mode", "write", "list");
            params.sendParameter("firstRowTitles", "true", "list");
            params.sendParameter("xcom", step1,"list");
            params.sendParameter("rowDelimiter", ";", "input");
            params.selectTab("//*[@id=\"file_li\"]/a");
            params.selectFile("filepath","/selenium/salida.txt");
            params.save();
            canvas.save();
            canvas.close();
        	jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step2, jarname, dagname,jobsPage);
        	if(status.isEmpty()) {
                String hostPath = Paths.get("selenium/salida.txt").toAbsolutePath().toString();
                String content = new String(Files.readAllBytes(Paths.get(hostPath)));
                if(content.trim().equals("output\ntesting1")) {
                    // Eliminar el archivo después de verificar su contenido
                    Files.deleteIfExists(Paths.get(hostPath));
                    
                    authenticatedPage.goToJobs();
                    authenticatedPage.logout();
                    Assertions.assertTrue(true); 
                } else {
                    Assertions.fail("El contenido del archivo no coincide. Esperado: 'testing', Obtenido: '" + content + "'");
                }
        	} else {        
        		Assertions.fail("Problema al ejecutar el operador?");
        	}
        }
    }
    @Test(priority = 9)
    public void writeFileWoTitleWRDelimiterOperator() throws InterruptedException, IOException {
        log.info("writeFileWoTitleWRDelimiterOperator");
    	String dagname = "TEST_FILE_WRITE_DAG";
    	String step1 = "step0";
    	String step2 = "step1";
        String group = "group.test";
        String jarname = "writeFileWTitleWoRDelimiterOperator.jar";
        String cmd1 = "return \"testing1\"";
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
            
        	canvas.addStep(dagname,step2,"main.cl.dagserver.infra.adapters.operators.FileOperator");
        	EditorParamModal params = canvas.selectStage(step2);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("mode", "write", "list");
            params.sendParameter("firstRowTitles", "false", "list");
            params.sendParameter("xcom", step1,"list");
            params.sendParameter("rowDelimiter", ";", "input");
            params.selectTab("//*[@id=\"file_li\"]/a");
            params.selectFile("filepath","/selenium/salida.txt");
            params.save();
            canvas.save();
            canvas.close();
        	jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step2, jarname, dagname,jobsPage);
        	if(status.isEmpty()) {
                String hostPath = Paths.get("selenium/salida.txt").toAbsolutePath().toString();
                String content = new String(Files.readAllBytes(Paths.get(hostPath)));
                if(content.trim().equals("testing1")) {
                    // Eliminar el archivo después de verificar su contenido
                    Files.deleteIfExists(Paths.get(hostPath));
                    
                    authenticatedPage.goToJobs();
                    authenticatedPage.logout();
                    Assertions.assertTrue(true); 
                } else {
                    Assertions.fail("El contenido del archivo no coincide. Esperado: 'testing', Obtenido: '" + content + "'");
                }
        	} else {        
        		Assertions.fail("Problema al ejecutar el operador?");
        	}
        }
    }
    @Test(priority = 10)
    public void canBeExecutedInGroovyTest() throws InterruptedException {
        log.info("canBeExecutedInGroovyTest");
    	String dagname = "TEST_EXECUTED_BY_GROOVY_DAG";
        String step = "step1";
        String group = "group.test";
        String jarname = "canBeExecutedInGroovyTest.jar";
        String cmd1 = "def args = new Properties();def optionals = new Properties();" +
                  "args.setProperty(\"mode\",\"read\" );"+
                  "args.setProperty(\"firstRowTitles\",\"true\" );"+
                  "args.setProperty(\"filepath\",\"/prueba.csv\" );"+
                  "return operator.setArgs(args).setOptionals(optionals).setOperator(\"FileOperator\").execute()";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
        	JobsPage jobsPage = authenticatedPage.goToJobs();
        	this.createGroovyJob(jobsPage, dagname, step, group, jarname, cmd1);
        	jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step, jarname, dagname,jobsPage);
        	if(!status.isEmpty()) {
        		if(status.getJSONObject(0).get("content").equals("prueba;prueba2\nvalor1;valor2\n")) {
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
    public void fileCanBeOutputStepTest() throws InterruptedException {
    	log.info("fileCanBeOutputStepTest");
    	String dagname = "TEST_FILE1_DAG";
        String step1 = "step0";
        String step2 = "step1";
        String group = "group.test";
        String jarname = "filetest1.jar";
        String cmd1 = "return \"/prueba.csv\"";
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

            canvas.addStep(dagname,step2,"main.cl.dagserver.infra.adapters.operators.FileOperator");
            EditorParamModal params = canvas.selectStage(step2);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("mode", "read", "list");
            params.sendParameter("firstRowTitles", "true", "list");
            params.selectTab("//*[@id=\"file_li\"]/a");
            params.selectFile("filepath","${step0}");
            params.save();
            canvas.save();
            canvas.close();
        	jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step2, jarname, dagname,jobsPage);
        	if(!status.isEmpty()) {
        		if(status.getJSONObject(0).getString("content").contains(";")) {
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
