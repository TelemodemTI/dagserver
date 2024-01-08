package main.cl.dagserver.infra.adapters.operators;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.Dagmap;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;

@Operator(args={"cmd"})
public class CmdOperator extends OperatorStage {

	@Override
	public List<Dagmap> call() throws DomainException {		
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
		    List<Dagmap> list = new ArrayList<>();
		    Dagmap rv = new Dagmap();
		    rv.put("output", sbuilder.toString());
		    list.add(rv);
			return list;	
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}
	
	@Override
	public JSONObject getMetadataOperator() {
		MetadataManager metadata = new MetadataManager("main.cl.dagserver.infra.adapters.operators.CmdOperator");
		metadata.setParameter("cmd", "sourcecode");
		return metadata.generate();
	}
	@Override
	public String getIconImage() {
		return "cmd.png";
	}

}
