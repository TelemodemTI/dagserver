package main.cl.dagserver.integration.test;

import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;

import main.cl.dagserver.integration.pom.AuthenticatedPage;
import main.cl.dagserver.integration.pom.JobsPage;
import main.cl.dagserver.integration.pom.LoginPage;
import main.cl.dagserver.integration.pom.segments.CanvasDagEditor;
import main.cl.dagserver.integration.pom.segments.EditorParamModal;
import main.cl.dagserver.integration.test.core.BaseOperatorTest;

public class OperatorFileTest extends BaseOperatorTest {

    @Test(priority = 1)
    public void readFileWTitleWoRDelimiterOperator() throws InterruptedException {
        String dagname = "TEST_FILE1_DAG";
        String step = "step1";
        String group = "group.test";
        String jarname = "filetest1.jar";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
        	AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
        	JobsPage jobsPage = authenticatedPage.goToJobs();
        	CanvasDagEditor canvas = createJob(jobsPage,jarname, dagname, group, step);
            canvas.addStep(dagname,step,"main.cl.dagserver.infra.adapters.operators.FileOperator");
            EditorParamModal params = canvas.selectStage(step);
            params.selectTab("//*[@id=\"home_li\"]/a");
            params.sendParameter("mode", "read", "list");
            params.sendParameter("firstRowTitles", "true", "list");
            params.sendParameter("rowDelimiter", ";", "input");
            params.selectTab("//*[@id=\"file_li\"]/a");
            params.selectFile("/prueba.csv");
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
    @Test(priority = 2)
    public void readFileWoTitleWoRDelimiterOperator() {
        //lectura de archivo existe, sin titulos, sin delimitador
    }
    @Test(priority = 3)
    public void readFileWTitleWRDelimiterOperator() {
        //lectura de archivo existe, con titulos, con delimitador
    }
    @Test(priority = 4)
    public void readFileWoTitleWRDelimiterOperator() {
        //lectura de archivo existe, sin titulos, con delimitador
    }
    @Test(priority = 5)
    public void readFileWoFileOperator() {
        //lectura de archivo no existe
    }
    @Test(priority = 6)
    public void writeFileWTitleWoRDelimiterOperator() {
        //escritura de archivo existe, con titulos, sin delimitador
    }
    @Test(priority = 7)
    public void writeFileWoTitleWoRDelimiterOperator() {
        //escritura de archivo existe, sin titulos, sin delimitador
    }
    @Test(priority = 8)
    public void writeFileWTitleWRDelimiterOperator() {
        //escritura de archivo existe, con titulos, con delimitador
    }
    @Test(priority = 9)
    public void writeFileWoTitleWRDelimiterOperator() {
        //escritura de archivo existe, sin titulos, con delimitador
    }
    
}
