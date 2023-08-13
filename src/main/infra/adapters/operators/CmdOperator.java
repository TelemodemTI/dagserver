package main.infra.adapters.operators;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

import org.json.JSONArray;
import org.json.JSONObject;

import main.domain.annotations.Operator;
import main.domain.core.DagExecutable;
import main.domain.exceptions.DomainException;
import main.infra.adapters.input.graphql.types.OperatorStage;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodCall;

@Operator(args={"prefix","c","cmd"})
public class CmdOperator extends OperatorStage implements Callable<StringBuilder> {

	@Override
	public StringBuilder call() throws DomainException {		
		try {
			ProcessBuilder builder = new ProcessBuilder(this.args.getProperty("prefix"), this.args.getProperty("c"), this.args.getProperty("cmd"));
		    builder.redirectErrorStream(true);
		    Process p = builder.start();
		    BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
		    String line;
		    StringBuilder sbuilder = new StringBuilder();
		    while (true) {
		            line = r.readLine();
		            if (line == null) { break; }
		            sbuilder.append(line + System.lineSeparator());
		    }
			return sbuilder;	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	@Override
	public Implementation getDinamicInvoke(String stepName,String propkey, String optkey) throws DomainException {
		try {
			return MethodCall.invoke(DagExecutable.class.getDeclaredMethod("addOperator", String.class, Class.class, String.class)).with(stepName, CmdOperator.class,propkey);
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
		
    }
	@Override
	public JSONObject getMetadataOperator() {
		JSONArray params = new JSONArray();
		params.put(new JSONObject("{name:\"prefix\",type:\"text\"}"));
		params.put(new JSONObject("{name:\"c\",type:\"text\"}"));
		params.put(new JSONObject("{name:\"cmd\",type:\"sourcecode\"}"));
		
		JSONObject tag = new JSONObject();
		tag.put("class", "main.infra.adapters.operators.CmdOperator");
		tag.put("name", "CmdOperator");
		tag.put("params", params);
		return tag;
	}
	@Override
	public String getIconImage() {
		return "cmd.png";
	}
}
