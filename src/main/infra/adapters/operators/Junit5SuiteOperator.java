package main.infra.adapters.operators;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.json.JSONObject;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import main.domain.annotations.Operator;
import main.infra.adapters.input.graphql.types.OperatorStage;
import net.bytebuddy.implementation.Implementation;

@Operator(args={"suiteClass"})
public class Junit5SuiteOperator extends OperatorStage implements Callable<List<Map<String, Object>>> {

    @Override
    public List<Map<String, Object>> call() throws Exception {
    	List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
    	Map<String, Object> item = new HashMap<String, Object>();
    	result.add(item);
    	
        String suiteClassName = args.getProperty("suiteClass");
        Class<?> suiteClass = Class.forName(suiteClassName);
        ClassSelector classSelector = DiscoverySelectors.selectClass(suiteClass);
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(classSelector)
                .build();
        Launcher launcherFactory = LauncherFactory.create();
        launcherFactory.execute(request,this.getListener(item));
        return result;
    }
    
    private TestExecutionListener getListener(Map<String, Object> item) {
    	return new TestExecutionListener() {
            @Override
            public void executionStarted(TestIdentifier testIdentifier) {
            	item.put("displayName", testIdentifier.getDisplayName());
            	item.put("startAt", new Date());
            }

            @Override
            public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
                item.put("status", testExecutionResult.getStatus());
                item.put("endAt", new Date());
            }
        };
    }
    @Override
	public Implementation getDinamicInvoke(String stepName,String propkey, String optkey) throws Exception {
    	return null;
    }

	@Override
	public JSONObject getMetadataOperator() {
		return null;
	}
}
