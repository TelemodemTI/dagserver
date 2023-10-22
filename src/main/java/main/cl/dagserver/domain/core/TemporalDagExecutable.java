package main.cl.dagserver.domain.core;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.json.JSONObject;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

		logdag = LoggerFactory.getLogger(evalstring);
		
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
		while (breadthFirstIterator.hasNext()) {
			
			DagNode node = breadthFirstIterator.next();
			status.put(node.name, OperatorStatus.EXECUTING);
			logdag.debug("executing node::"+node.name);
			var statusToBe = this.constraints.get(node.name);
			if(statusToBe != null) {
				logdag.debug("constraint node::"+statusToBe.toString());	
			} else {
				logdag.debug("no constraint");
			}
			Class<?> clazz = node.operator;
			ExecutorService executorService = Executors.newSingleThreadExecutor();
			Future<?> future = executorService.submit(() -> {
			    try {
			    	Map<String,Object> args = new HashMap<>();
			    	args.put("evalstring", evalstring);
			    	args.put("clazz", clazz);
			    	args.put("node", node);
			    	args.put("xcom", xcom);
			    	args.put("statusToBe", statusToBe);
			    	args.put("logdag", logdag);
			    	args.put("status", status);
			    	args.put("fa", fa);
					this.instanciateEvaluate(args,parmdata,timestamps);
				} catch (JobExecutionException e) {
					logdag.error(e.getMessage());
				}
			});
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
		
		
		//Logger.getRootLogger().removeAppender(fa);
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
