package main.cl.dagserver.infra.adapters.operators;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.json.JSONObject;

import com.nhl.dflib.DataFrame;

import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;

@Operator(args={"cmd"})
public class CmdOperator extends OperatorStage {

	@Override
	public DataFrame call() throws DomainException {		
		try {
			ProcessBuilder builder;
			String os = System.getProperty("os.name").toLowerCase();
			if (os.contains("win")) {
				// Windows
				builder = new ProcessBuilder("cmd", "/c", this.args.getProperty("cmd"));
			} else {
				// Unix/Linux/Mac
				builder = new ProcessBuilder("sh", "-c", this.args.getProperty("cmd"));
			}
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
		    return DataFrame
	        .byArrayRow("output") 
	        .appender() 
	        .append(sbuilder.toString())   
	        .toDataFrame();
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}
	
	@Override
	public JSONObject getMetadataOperator() {
		MetadataManager metadata = new MetadataManager("main.cl.dagserver.infra.adapters.operators.CmdOperator");
		metadata.setType("PROCCESS");
		metadata.setParameter("cmd", "sourcecode", Arrays.asList("application/x-sh"));
		return metadata.generate();
	}
	@Override
	public String getIconImage() {
		return "cmd.png";
	}
}
