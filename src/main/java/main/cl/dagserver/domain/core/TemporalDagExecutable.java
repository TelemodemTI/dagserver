package main.cl.dagserver.domain.core;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.json.JSONObject;
import org.apache.log4j.Logger;


import main.cl.dagserver.domain.enums.OperatorStatus;
import main.cl.dagserver.domain.exceptions.DomainException;

public class TemporalDagExecutable extends DagExecutable  {
	
	private Logger logdag;
	private JSONObject xcom;
	private String logText;
	private String evalstring;
	private Map<String,OperatorStatus> status;
	private Date evalDt;
	public TemporalDagExecutable() {
		super();
		status = new HashMap<>();
	}
	public void setDagname(String dagname) {
		this.dagname = dagname;
	}
	@Override
	public void addOperator(String name,Class<?> operator,Properties args,Properties optionals) throws DomainException {
		super.addOperator(name,operator,args,optionals);
	}
	@Override
	public void addDependency(String name1, String name2, String status) {
		super.addDependency(name1, name2, status);
	}
	public String execute(String stopAtStep) {
		this.executionSource = "UNCOMPILED_JOB_ENGINE";	
		this.isRunning = true;
		var statusf = this.evaluate(stopAtStep);
		this.isRunning = false;
		return statusf.toString();
	}
	protected OperatorStatus evaluate(String stopAtStep) {
		evalDt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		evalstring = this.generateRandomString(12)+"_"+sdf.format(evalDt);
		var fa = this.createDagMemoryAppender(evalstring);
		xcom = new JSONObject();
		logdag = Logger.getLogger(evalstring);
		logdag.debug("executing dag::"+this.dagname);
		BreadthFirstIterator<DagNode, DefaultEdge> breadthFirstIterator  = new BreadthFirstIterator<>(g);
		Map<String,String> parmdata = new HashMap<>();
		List<String> timestamps = new ArrayList<>();
		parmdata.put("evalkey",evalstring);
		parmdata.put("dagname",dagname);
		parmdata.put("value",fa.getResult());
		parmdata.put("xcom",null);
		parmdata.put("channel","TEST_API");
		String obj = (stopAtStep.trim().isEmpty())?"COMPLETE":stopAtStep;
		parmdata.put("objetive",obj);
		parmdata.put("sourceType","UNCOMPILED");
		String stepf = "";
		while (breadthFirstIterator.hasNext()) {
			
			DagNode node = breadthFirstIterator.next();
			status.put(node.name, OperatorStatus.EXECUTING);
			logdag.debug("preparing node::"+node.name);
			
			
			
			
			var keys = this.constraints.keySet();
			List<OperatorStatus> statusNow = new ArrayList<>();
			for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
				String string = (String) iterator.next();
				if(string.endsWith("."+node.name)) {
					stepf = string.split("\\.")[0];
					statusNow.add(this.constraints.get(string));	
				}
			}
			Map<String,Object> argsr = new HashMap<>();
			argsr.put("evalstring", evalstring);
			if(statusNow.size() > 0) {
				logdag.debug("constraint node::"+statusNow.toString());	
				argsr.put("statusToBe", statusNow.get(0));
			} else {
				logdag.debug("no constraint");
			}
			argsr.put("xcom", xcom);
			argsr.put("stepf", stepf);
			Future<?> future = this.futureDelegate(node, logdag, parmdata, timestamps, argsr, fa, status);
			while (!future.isDone()) {
			    try {
			    	Thread.sleep(500);	
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			if(node.name.equals(stopAtStep)) {
				break;
			}
		}
		logText = fa.getResult();
		fa.close();
		
		return OperatorStatus.OK;
	}
	public Logger getLogdag() {
		return logdag;
	}
	public void setLogdag(Logger logdag) {
		this.logdag = logdag;
	}
	public JSONObject getXcom() {
		return xcom;
	}
	public void setXcom(JSONObject xcom) {
		this.xcom = xcom;
	}
	public String getLogText() {
		return logText;
	}
	public void setLogText(String logText) {
		this.logText = logText;
	}
	public String getEvalstring() {
		return evalstring;
	}
	public void setEvalstring(String evalstring) {
		this.evalstring = evalstring;
	}
	public Map<String, OperatorStatus> getStatus() {
		return status;
	}
	public void setStatus(Map<String, OperatorStatus> status) {
		this.status = status;
	}
	public Date getEvalDt() {
		return evalDt;
	}
	public void setEvalDt(Date evalDt) {
		this.evalDt = evalDt;
	}
	
}
