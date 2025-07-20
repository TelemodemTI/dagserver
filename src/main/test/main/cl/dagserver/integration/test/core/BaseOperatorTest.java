package main.cl.dagserver.integration.test.core;

import org.json.JSONArray;

import main.cl.dagserver.integration.pom.AuthenticatedPage;
import main.cl.dagserver.integration.pom.JobsPage;
import main.cl.dagserver.integration.pom.KeystorePage;
import main.cl.dagserver.integration.pom.segments.CanvasDagEditor;
import main.cl.dagserver.integration.pom.segments.EditorParamModal;

public class BaseOperatorTest extends BaseIntegrationTest {

	
    protected CanvasDagEditor createJob(JobsPage jobsPage,String jarname, String dagname, String group) throws InterruptedException {
        
        var canvas = jobsPage.createNewJobCanvas();
        canvas.setName(jarname);
        canvas.createNoneDag(dagname, group);    
        return canvas;
    }
    
    protected void createGroovyJob(JobsPage jobsPage,String dagname, String step, String group, String jarname, String cmd1) throws InterruptedException {
		CanvasDagEditor canvas = createJob(jobsPage,jarname, dagname, group);
        canvas.addStep(dagname,step,"main.cl.dagserver.infra.adapters.operators.GroovyOperator");
        EditorParamModal params = canvas.selectStage(step);
        params.selectTab("//*[@id=\"profile_li\"]/a");
        params.sendScript(cmd1);
        params.save();
        canvas.saveJar();
	}
    
    protected JSONArray executeDesign(String step, String jarname, String dagname,JobsPage jobsPage) throws InterruptedException {
		var uncompileds = jobsPage.goToUncompiledTab();
		uncompileds.searchUncompiled(jarname);
		var canvas = uncompileds.editDesign(jarname);
		canvas.selectDag(dagname);
		var modal = canvas.execute();
		var result = modal.save();
		String resultstr = result.getOutputXcom(step);
		result.close();
		JSONArray status = new JSONArray(resultstr);
		return status;
	}
    protected void createKeystore(AuthenticatedPage authenticatedPage,String alias, String username, String pwd) throws InterruptedException {
    	Thread.sleep(3000);
    	KeystorePage keystorePage = authenticatedPage.goToKeystore();
        var modal = keystorePage.openNewKeystoreEntryModal();
        modal.create(alias, username, pwd);
        keystorePage.search(alias);
        authenticatedPage.goToJobs();
    }
}
