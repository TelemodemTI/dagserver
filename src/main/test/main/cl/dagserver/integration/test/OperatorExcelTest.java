package main.cl.dagserver.integration.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;

import main.cl.dagserver.integration.pom.AuthenticatedPage;
import main.cl.dagserver.integration.pom.JobsPage;
import main.cl.dagserver.integration.pom.LoginPage;
import main.cl.dagserver.integration.pom.segments.CanvasDagEditor;
import main.cl.dagserver.integration.pom.segments.EditorParamModal;
import main.cl.dagserver.integration.pom.segments.JobsUncompiledTab;
import main.cl.dagserver.integration.test.core.BaseOperatorTest;

public class OperatorExcelTest extends BaseOperatorTest {

    @Test(priority = 1)
    public void readXLSExcelOperator() throws InterruptedException {
        String dagname = "TEST_FILE1_DAG";
        String step = "step1";
        String group = "group.test";
        String jarname = "filetest1.jar";
        String sheetName = "Hoja 1";
        String startRow = "0";
        String startColumn = "0";
        String filepath = "/planilla.xls";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
        	JobsPage jobsPage = authenticatedPage.goToJobs();
        	CanvasDagEditor canvas = createJob(jobsPage,jarname, dagname, group);

            canvas.addStep(dagname,step,"main.cl.dagserver.infra.adapters.operators.ExcelOperator");
            EditorParamModal params = canvas.selectStage(step);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("mode", "read", "list");
            params.sendParameter("includeTitles", "true", "list");
            params.sendParameter("sheetName", sheetName,"input");
    		params.sendParameter("startRow", startRow,"input");
    		params.sendParameter("startColumn", startColumn,"input");
    		params.save();
            canvas.saveJar();
            
            jobsPage = authenticatedPage.goToJobs();
            JobsUncompiledTab uncompileds = jobsPage.goToUncompiledTab();
            uncompileds.searchUncompiled(jarname);
            canvas = uncompileds.editDesign(jarname);
            canvas.selectDag(dagname);
            params = canvas.selectStage(step);
            params.selectTab("//*[@id=\"file_li\"]/a");
            params.selectFile("filePath",filepath);
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
    public void readXLSXExcelOperator() throws InterruptedException {
    	String dagname = "TEST_FILE1_DAG";
        String step = "step1";
        String group = "group.test";
        String jarname = "filetest1.jar";
        String sheetName = "Hoja 1";
        String startRow = "0";
        String startColumn = "0";
        String filepath = "/planilla.xlsx";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
        	JobsPage jobsPage = authenticatedPage.goToJobs();
        	CanvasDagEditor canvas = createJob(jobsPage,jarname, dagname, group);

            canvas.addStep(dagname,step,"main.cl.dagserver.infra.adapters.operators.ExcelOperator");
            EditorParamModal params = canvas.selectStage(step);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("mode", "read", "list");
            params.sendParameter("includeTitles", "true", "list");
            params.sendParameter("sheetName", sheetName,"input");
    		params.sendParameter("startRow", startRow,"input");
    		params.sendParameter("startColumn", startColumn,"input");
    		params.save();
            canvas.saveJar();
            
            jobsPage = authenticatedPage.goToJobs();
            JobsUncompiledTab uncompileds = jobsPage.goToUncompiledTab();
            uncompileds.searchUncompiled(jarname);
            canvas = uncompileds.editDesign(jarname);
            canvas.selectDag(dagname);
            params = canvas.selectStage(step);
            params.selectTab("//*[@id=\"file_li\"]/a");
            params.selectFile("filePath",filepath);
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

    @Test(priority = 3)
    public void writeXLSExcelOperator() throws InterruptedException, IOException {
    	
    	String dagname = "TEST_FILE_WRITE_DAG";
    	String step1 = "step0";
    	String step2 = "step1";
        String group = "group.test";
        String sheetName = "Hoja 1";
        String startRow = "0";
        String startColumn = "0";
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
            
        	canvas.addStep(dagname,step2,"main.cl.dagserver.infra.adapters.operators.ExcelOperator");
            params = canvas.selectStage(step2);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("mode", "write", "list");
            params.sendParameter("includeTitles", "true", "list");
            params.sendParameter("xcom", step1,"list");
            params.sendParameter("sheetName", sheetName,"input");
    		params.sendParameter("startRow", startRow,"input");
    		params.sendParameter("startColumn", startColumn,"input");
            params.selectTab("//*[@id=\"file_li\"]/a");
            params.selectFile("filePath","/selenium/salida.xls");
            params.save();
            canvas.save();
            canvas.close();
        	jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step2, jarname, dagname,jobsPage);
        	if(!status.isEmpty()) {
        		String hostPath = Paths.get("selenium/salida.xls").toAbsolutePath().toString();
        		if (Files.exists(Paths.get(hostPath)) && Files.size(Paths.get(hostPath)) > 0) {
        			Files.deleteIfExists(Paths.get(hostPath));
                    authenticatedPage.goToJobs();
                    authenticatedPage.logout();
                    Assertions.assertTrue(true);
        		}
        	} else {        
        		Assertions.fail("Problema al ejecutar el operador?");
        	}
        }
    	
    	
    }

    @Test(priority = 4)
    public void writeXLSXExcelOperator() throws InterruptedException, IOException {
    	
    	String dagname = "TEST_FILE_WRITE_DAG";
    	String step1 = "step0";
    	String step2 = "step1";
        String group = "group.test";
        String sheetName = "Hoja 1";
        String startRow = "0";
        String startColumn = "0";
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
            
        	canvas.addStep(dagname,step2,"main.cl.dagserver.infra.adapters.operators.ExcelOperator");
            params = canvas.selectStage(step2);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("mode", "write", "list");
            params.sendParameter("includeTitles", "true", "list");
            params.sendParameter("xcom", step1,"list");
            params.sendParameter("sheetName", sheetName,"input");
    		params.sendParameter("startRow", startRow,"input");
    		params.sendParameter("startColumn", startColumn,"input");
            params.selectTab("//*[@id=\"file_li\"]/a");
            params.selectFile("filePath","/selenium/salida.xlsx");
            params.save();
            canvas.save();
            canvas.close();
        	jobsPage = authenticatedPage.goToJobs();
        	var status = executeDesign(step2, jarname, dagname,jobsPage);
        	if(!status.isEmpty()) {
        		String hostPath = Paths.get("selenium/salida.xlsx").toAbsolutePath().toString();
        		if (Files.exists(Paths.get(hostPath)) && Files.size(Paths.get(hostPath)) > 0) {
        			Files.deleteIfExists(Paths.get(hostPath));
                    authenticatedPage.goToJobs();
                    authenticatedPage.logout();
                    Assertions.assertTrue(true);
        		}
        	} else {        
        		Assertions.fail("Problema al ejecutar el operador?");
        	}
        }
    }

    @Test(priority = 5)
    public void canBeExecutedInGroovyTest() throws InterruptedException {
    	
    	String dagname = "TEST_EXECUTED_BY_GROOVY_DAG";
        String step = "step1";
        String group = "group.test";
        String jarname = "canBeExecutedInGroovyTest.jar";
        String cmd1 = "def args = new Properties();def optionals = new Properties();" +
                  "args.setProperty(\"mode\",\"read\" );"+
                  "args.setProperty(\"filePath\",\"/planilla.xls\" );"+
                  "args.setProperty(\"includeTitles\",\"true\" );"+
                  "args.setProperty(\"sheetName\",\"Hoja1\" );"+
                  "args.setProperty(\"startRow\",\"0\" );"+
                  "args.setProperty(\"startColumn\",\"0\" );"+
                  "return operator.setArgs(args).setOptionals(optionals).setOperator(\"FileOperator\").execute()";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
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

    @Test(priority = 6)
    public void fileCanBeOutputStepTest() throws InterruptedException {
    	
    	String dagname = "TEST_FILE_WRITE_DAG";
    	String step1 = "step0";
    	String step2 = "step1";
        String group = "group.test";
        String sheetName = "Hoja 1";
        String startRow = "0";
        String startColumn = "0";
        String jarname = "writeFileWTitleWoRDelimiterOperator.jar";
        String cmd1 = "return \"/planilla.xls\"";
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
            
        	canvas.addStep(dagname,step2,"main.cl.dagserver.infra.adapters.operators.ExcelOperator");
            params = canvas.selectStage(step2);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("mode", "read", "list");
            params.sendParameter("includeTitles", "true", "list");
            params.sendParameter("sheetName", sheetName,"input");
    		params.sendParameter("startRow", startRow,"input");
    		params.sendParameter("startColumn", startColumn,"input");
            params.selectTab("//*[@id=\"file_li\"]/a");
            params.selectFile("filePath","${step0}");
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

    @Test(priority = 7)
    public void sheetNameCanBeOutputStepTest() throws InterruptedException {

    	String dagname = "TEST_FILE_WRITE_DAG";
    	String step1 = "step0";
    	String step2 = "step1";
        String group = "group.test";
        String sheetName = "Hoja 1";
        String startRow = "0";
        String startColumn = "0";
        String jarname = "writeFileWTitleWoRDelimiterOperator.jar";
        String cmd1 = "return \""+sheetName+"\"";
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
            
        	canvas.addStep(dagname,step2,"main.cl.dagserver.infra.adapters.operators.ExcelOperator");
            params = canvas.selectStage(step2);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("mode", "read", "list");
            params.sendParameter("includeTitles", "true", "list");
            params.sendParameter("sheetName", "${step0}","input");
    		params.sendParameter("startRow", startRow,"input");
    		params.sendParameter("startColumn", startColumn,"input");
            params.selectTab("//*[@id=\"file_li\"]/a");
            params.selectFile("filePath","/planilla.xls");
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
