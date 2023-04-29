package main.infra.adapters.operators;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

import main.domain.annotations.Operator;
import main.domain.types.OperatorStage;

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
}
