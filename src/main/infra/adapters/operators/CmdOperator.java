package main.infra.adapters.operators;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

import main.domain.annotations.Operator;
import main.domain.core.DagExecutable;
import main.infra.adapters.input.graphql.types.OperatorStage;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodCall;

@Operator(args={"prefix","c","cmd"})
public class CmdOperator extends OperatorStage implements Callable<StringBuilder> {

	@Override
	public StringBuilder call() throws Exception {		
		
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
	}
	@Override
	public Implementation getDinamicInvoke(String stepName,String propkey, String optkey) throws Exception {
		Implementation implementation = MethodCall.invoke(DagExecutable.class.getConstructor())				
				.andThen(
						MethodCall.invoke(DagExecutable.class.getDeclaredMethod("addOperator", String.class, Class.class, String.class)).with(stepName, CmdOperator.class,propkey)
				);
		return implementation;
    }
}
