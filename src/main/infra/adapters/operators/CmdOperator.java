package main.infra.adapters.operators;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import org.json.JSONObject;
import main.domain.annotations.Operator;
import main.domain.core.MetadataManager;
import main.domain.core.OperatorStage;
import main.domain.exceptions.DomainException;

@Operator(args={"cmd"})
public class CmdOperator extends OperatorStage implements Callable<StringBuilder> {

	@Override
	public StringBuilder call() throws DomainException {		
		try {
			ProcessBuilder builder = new ProcessBuilder("cmd", "/c",this.args.getProperty("cmd"));
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
	public JSONObject getMetadataOperator() {
		MetadataManager metadata = new MetadataManager("main.infra.adapters.operators.CmdOperator");
		metadata.setParameter("cmd", "sourcecode");
		return metadata.generate();
	}
	@Override
	public String getIconImage() {
		return "cmd.png";
	}

}
