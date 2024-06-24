package main.cl.dagserver.infra.adapters.operators;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import joinery.DataFrame;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;

@Operator(args={"cmd"})
public class CmdOperator extends OperatorStage {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public DataFrame call() throws DomainException {		
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
		    List<Map<String,Object>> list = new ArrayList<>();
		    Map<String,Object> map = new HashMap<String,Object>();
		    DataFrame rv = new DataFrame();
		    map.put("output", sbuilder.toString());
		    list.add(map);
		    rv.add(list);
			return rv;	
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
