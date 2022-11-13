package main.domain.types;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

public class OperatorStage {
	protected static Logger log = Logger.getLogger("DAG");
	protected String name;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	protected Properties args;
	protected Map<String,Object> xcom = new HashMap<String,Object>();
	
	public Properties getArgs() {
		return args;
	}

	public void setArgs(Properties args) {
		this.args = args;
	}

	public Map<String, Object> getXcom() {
		return xcom;
	}

	public void setXcom(Map<String, Object> xcom) {
		this.xcom = xcom;
	}
	
}
