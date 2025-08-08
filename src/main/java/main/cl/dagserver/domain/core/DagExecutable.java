package main.cl.dagserver.domain.core;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.jgrapht.Graph;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.json.JSONObject;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import com.nhl.dflib.DataFrame;

import lombok.Data;
import main.cl.dagserver.application.ports.output.SchedulerRepositoryOutputPort;
import main.cl.dagserver.domain.annotations.Dag;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.enums.OperatorStatus;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.infra.adapters.confs.ApplicationContextUtils;
import main.cl.dagserver.infra.adapters.confs.InMemoryLoggerAppender;
import main.cl.dagserver.infra.adapters.confs.QuartzConfig;

public class DagExecutable implements Job,JobListener  {
	
	public String getGroup() {
		Dag anno = this.getClass().getAnnotation(Dag.class);
		return anno.group();
	}

	private static final String VALUE = "value";
	private static final String EVALSTRING = "evalstring";
	private static final String STATUSTOBE = "statusToBe";
	
	@Data
	protected class DagNode {
		protected Class<?> operator;
		protected String name;
		protected Properties args;
		protected Properties optionals;
		DagNode(String name,Class<?> operator,Properties args,Properties optionals){
			this.name = name;
			this.operator = operator;
			this.args = args;
			this.optionals = optionals;
		}
	}
	
	private String eventname = "";
	private Map<String,DagNode> nodeList = new HashMap<>();
	
	protected Properties extrArgs;
	protected SchedulerRepositoryOutputPort repo;
	protected QuartzConfig quartzConfig;
	protected ApplicationEventPublisher eventPublisher;
	protected Map<String,OperatorStatus> constraints = new HashMap<>();
	protected Map<String,DataFrame> xcom = new HashMap<>();
	protected Boolean isRunning = true;
	protected String dagname = "";
	protected String executionSource = "";
	protected Graph<DagNode, DefaultEdge> g;
	protected JobDetail jobDetail;
	protected String channelData = "";
	
	public DagExecutable() {
		this.g = new DirectedAcyclicGraph<>(DefaultEdge.class);
		ApplicationContext appCtx = ApplicationContextUtils.getApplicationContext();
		if(appCtx!=null) {
			repo =  appCtx.getBean("schedulerRepository", SchedulerRepositoryOutputPort.class);
			quartzConfig = appCtx.getBean("quartzConfig", QuartzConfig.class);
			eventPublisher = appCtx;
		}
	}
	
	@SuppressWarnings("unused")
	public void execute(JobExecutionContext context) throws JobExecutionException {
		this.executionSource = context.getMergedJobDataMap().getString("channel");
		this.channelData = context.getMergedJobDataMap().getString("channelData");
		
		if(this.executionSource == null || this.executionSource.isEmpty()) {
			this.executionSource = "JOB_SCHEDULER";	
		}
		jobDetail = context.getJobDetail();
		this.isRunning = true;
		Dag anno = this.getClass().getAnnotation(Dag.class);
		this.dagname = anno.name();
		var status = this.evaluate();
		this.isRunning = false;
	}
	
	public void addExtraParams(Properties propxtra) {
		this.extrArgs = propxtra;
	}
	
	@SuppressWarnings("rawtypes")
	protected OperatorStatus evaluate() throws JobExecutionException {
		Map<String,OperatorStatus> status = new HashMap<>();
		Date evalDt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String evalstring = RandomGenerator.generateRandomString(12)+"_"+sdf.format(evalDt);
		List<String> timestamps = new ArrayList<>();
		var fa = this.createDagMemoryAppender(evalstring);
		Map<String,Object> data = new HashMap<>();
		data.put("channelData", this.channelData);
		DataFrame dfdata = DataFrameUtils.buildDataFrameFromMap(Arrays.asList(data));
		//Map<String,DataFrame> xcom = new HashMap<>();
		xcom.put("args", dfdata);
		Logger logdag = Logger.getLogger(evalstring);
		logdag.setLevel(Level.DEBUG);
		logdag.debug("executing dag::"+this.dagname);
		Map<String,String> parmdata = new HashMap<>(); 
		parmdata.put("evalkey",evalstring);
		parmdata.put("dagname",dagname);
		parmdata.put(VALUE,fa.getResult());
		parmdata.put("xcom",null);
		parmdata.put("channel",this.executionSource);
		parmdata.put("objetive","COMPLETE");
		parmdata.put("sourceType","COMPILED");
		repo.setLog(parmdata,status,timestamps);
		String stepf = "";
		BreadthFirstIterator breadthFirstIterator  = new BreadthFirstIterator<>(g);
		while (breadthFirstIterator.hasNext()) {
			DagNode node = (DagNode) breadthFirstIterator.next();
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
			argsr.put(EVALSTRING, evalstring);
			parmdata.put(VALUE,fa.getResult());
			repo.setLog(parmdata,status,timestamps);
			
			if(statusNow.size() > 0) {
				logdag.debug("constraint node::"+statusNow.toString());	
				argsr.put(STATUSTOBE, statusNow.get(0));
			} else {
				logdag.debug("no constraint");
			}
			argsr.put("xcom", xcom);
			argsr.put("stepf", stepf);
			Future<?> future = this.futureDelegate(node, logdag, parmdata, timestamps, argsr, fa, status);
			stepf = node.name;
			try {
				future.get(); 
			    parmdata.put(VALUE,fa.getResult());
			    Long dt = new Date().getTime();
			    timestamps.add(dt.toString());
			    repo.setLog(parmdata,status,timestamps);	
			} catch (InterruptedException e) {
			    Thread.currentThread().interrupt();
			} catch (Exception e) {
				eventPublisher.publishEvent(new ExceptionEventLog(this, new DomainException(e), "evaluate",evalstring));
				throw new JobExecutionException(e);
			}
		}
		return this.setLogEvaluate(fa, xcom, status,parmdata,timestamps);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Future futureDelegate(
	        DagNode node,
	        Logger logdag,
	        Map<String, String> parmdata,
	        List<String> timestamps,
	        Map<String, Object> argsr,
	        InMemoryLoggerAppender fa,
	        Map<String, OperatorStatus> status) {
		Map<String,DataFrame> xcom = (Map<String,DataFrame>) argsr.get("xcom");
	    String evalstring = (String) argsr.get(EVALSTRING);
	    String stepf = (String) argsr.get("stepf");
	    OperatorStatus statusToBe = (OperatorStatus) argsr.get(STATUSTOBE);
	    Class<?> clazz = node.operator;
	    ExecutorService executorService = Executors.newSingleThreadExecutor();
	    return executorService.submit(() -> {
	        try {
	            Map<String, Object> args = new HashMap<>();
	            args.put(EVALSTRING, evalstring);
	            args.put("clazz", clazz);
	            args.put("node", node);
	            args.put("xcom", xcom);
	            args.put(STATUSTOBE, statusToBe);
	            args.put("logdag", logdag);
	            args.put("status", status);
	            args.put("stepf", stepf);
	            args.put("fa", fa);
	            this.instanciateEvaluate(args, parmdata, timestamps);
	        } catch (JobExecutionException e) {
	            logdag.error(e);
	            eventPublisher.publishEvent(new ExceptionEventLog(this, new DomainException(e), "scheduler", evalstring));
	        }
	    });
	}
	
	@SuppressWarnings("unchecked")
	protected void instanciateEvaluate(Map<String,Object> args,Map<String,String> parmdata, List<String> timestamps) throws JobExecutionException {
		Class<?> clazz = (Class<?>) args.get("clazz");
		DagNode node = (DagNode) args.get("node");
		Map<String,DataFrame> xcom = (Map<String,DataFrame>) args.get("xcom");
		String stepf = (String) args.get("stepf");
		OperatorStatus statusToBe = (OperatorStatus) args.get(STATUSTOBE);
		Logger logdag = (Logger) args.get("logdag");
		Map<String,OperatorStatus> status = (Map<String, OperatorStatus>) args.get("status");
		InMemoryLoggerAppender fa = (InMemoryLoggerAppender) args.get("fa");
		
		try {
			if( (statusToBe == null)  || statusToBe.equals(OperatorStatus.ANY)|| (status.get(stepf).equals(statusToBe))) {
				OperatorStage op = (OperatorStage) clazz.getDeclaredConstructor().newInstance();
				op.setArgs(node.args);
				op.setXcom(xcom);
				op.setName(node.name);
				op.setOptionals(node.optionals);
				Callable<DataFrame> instance  = (Callable<DataFrame>) op; 
				DataFrame result = instance.call();
				xcom.put(node.name , result );
				logdag.debug("::end execution ok::");
				status.put(node.name, OperatorStatus.OK);
			} else {
				logdag.debug("::execution skipped::");
				status.put(node.name, OperatorStatus.SKIPPED);
			}
		} catch (Exception e) {
			var keys = this.constraints.keySet();
			List<OperatorStatus> statusNow = new ArrayList<>();
			for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
				String string = iterator.next();
				if(string.startsWith(node.name+".")) {
					statusNow.add(this.constraints.get(string));	
				}
			}
			if(statusNow.contains(OperatorStatus.ERROR)) {
				status.put(node.name, OperatorStatus.ERROR);
				logdag.debug("error result::"+e.getMessage());
			} else {
				status.put(node.name, OperatorStatus.ERROR);
				logdag.error(e);
				Logger.getRootLogger().removeAppender(fa);
				try {
					String locatedAt = repo.createInternalStatus(xcom);
					parmdata.put(VALUE,fa.getResult());
					parmdata.put("xcom", locatedAt);
					repo.setLog(parmdata,status,timestamps);
				} catch (Exception e2) {
					throw new JobExecutionException(e2);	
				}
				throw new JobExecutionException(e);	
			}
		}
	}
	private OperatorStatus setLogEvaluate(InMemoryLoggerAppender fa,Map<String,DataFrame> xcom,Map<String,OperatorStatus> status,Map<String,String> parmdata,List<String> timestamps) throws JobExecutionException {
		try {
			fa.close();
			Logger.getRootLogger().removeAppender(fa);
			String locatedAt = repo.createInternalStatus(xcom);
			parmdata.put(VALUE,fa.getResult());
			parmdata.put("xcom", locatedAt);
			repo.setLog(parmdata,status,timestamps);
			return OperatorStatus.OK;	
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}
	protected InMemoryLoggerAppender createDagMemoryAppender(String name) {
		InMemoryLoggerAppender fa = new InMemoryLoggerAppender();
		fa.setName(name);
		fa.setLayout(new PatternLayout("%d %-5p [%c{1}] %m%n"));
		fa.setThreshold(Level.DEBUG);
		fa.activateOptions();
		Logger.getRootLogger().addAppender(fa);
		return fa;
	}
	protected void addOperator(String name,Class<?> operator) throws DomainException {
		try {
			this.addOperator(name, operator, new Properties() , new Properties());	
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}
	protected void addOperator(String name,Class<?> operator,Properties args) throws DomainException {
		try {
			this.addOperator(name, operator, args , new Properties());	
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}
	
	protected void addOperator(String name,Class<?> operator,String propertyKey) throws DomainException {
		try {
			Properties props = this.getDagProperties(propertyKey);
			Properties propsOpt = new Properties();
			this.addOperator(name, operator, props, propsOpt);	
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}
	
	protected void addOperator(String name,Class<?> operator,String propertyKey,String optionalsKey) throws DomainException {
		try {
			Properties props = this.getDagProperties(propertyKey);
			Properties propsOpt = this.getDagProperties(optionalsKey);
			this.addOperator(name, operator, props, propsOpt);	
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}
	
	protected void addOperator(String name,Class<?> operator,Properties args,Properties optionals) throws DomainException {
		Operator annotation = operator.getAnnotation(Operator.class);
		String[] argsarr = annotation.args();
		for (int i = 0; i < argsarr.length; i++) {
			String string = argsarr[i];
			if(!args.containsKey(string)) {
				throw new DomainException(new Exception(string + " not found in "+name+"::"+operator.getCanonicalName()));
			}
		}
		if(this.extrArgs != null) {
			args = this.mergePropertiesByIteratingKeySet(this.extrArgs,args);
		}
		var node = new DagNode(name,operator,args,optionals);
		this.nodeList.put(name, node);
		this.g.addVertex(node);
	}
	
	private Properties mergePropertiesByIteratingKeySet(Properties... properties) {
	    Properties mergedProperties = new Properties();
	    for (Properties property : properties) {
	        Set<String> propertyNames = property.stringPropertyNames();
	        for (String name : propertyNames) {
	            String propertyValue = property.getProperty(name);
	            mergedProperties.setProperty(name, propertyValue);
	        }
	    }
	    return mergedProperties;
	}
	
	protected void addDependency(String name1, String name2, String status) {
		status = status.isEmpty()?"ANY": status;
		this.addDependency(name1, name2, OperatorStatus.valueOf(status));
	}
	protected void addDependency(String name1, String name2, OperatorStatus status) {
		var node1 = this.nodeList.get(name1);
		var node2 = this.nodeList.get(name2);
		this.constraints.put(name1+"."+name2, status);
		this.g.addEdge(node1, node2);
	}

	public void setName(String dagname) {
		this.dagname = dagname;
	}
	
	@Override
	public String getName() {
		return this.dagname;
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		if(this.eventname.equals("onStart")) {
			try {
				this.executionSource = "JOB_LISTENER";
				this.quartzConfig.executeInmediate(this);
			} catch (DomainException e) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String evalkey = RandomGenerator.generateRandomString(12)+"_"+sdf.format(new Date());
				eventPublisher.publishEvent(new ExceptionEventLog(this, new DomainException(e), "jobToBeExecuted", evalkey));
			}	
		}
	}

	
	
	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		//not necesarry until now
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		if(this.eventname.equals("onEnd")) {
			try {
				this.executionSource = "JOB_LISTENER";
				this.quartzConfig.executeInmediate(this);
			} catch (DomainException e) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String evalkey = RandomGenerator.generateRandomString(12)+"_"+sdf.format(new Date());
				eventPublisher.publishEvent(new ExceptionEventLog(this, new DomainException(e), "jobWasExecuted", evalkey));
			}	
		}
	}

	public String getEventname() {
		return eventname;
	}

	public void setEventname(String eventname) {
		this.eventname = eventname;
	}
	
	public List<List<String>> getDagGraph()  throws DomainException {
		try {
			var info = new ArrayList<List<String>>();
			BreadthFirstIterator<DagNode, DefaultEdge> breadthFirstIterator  = new BreadthFirstIterator<>(g);
			while (breadthFirstIterator.hasNext()) {
				var detail = new ArrayList<String>();
				DagNode node = breadthFirstIterator.next();
				detail.add(node.name);
				detail.add(node.operator.getCanonicalName());
				

				Class<?> clase = Class.forName(node.operator.getCanonicalName());
				Constructor<?> constructor = clase.getDeclaredConstructor();
				OperatorStage instancia = (OperatorStage) constructor.newInstance();

				JSONObject props = new JSONObject(node.args);
				JSONObject opts = new JSONObject(node.optionals);
				detail.add(props.toString());
				detail.add(opts.toString());
				detail.add(instancia.getMetadataOperator().toString());
				info.add(detail);
			}
			return info;
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}
	
	protected Properties getDagProperties(String string) throws DomainException {
		return repo.getPropertiesFromDb(string);
	}

	
	
	public Boolean getIsRunning() {
		return isRunning;
	}

	public void setIsRunning(Boolean isRunning) {
		this.isRunning = isRunning;
	}
	
	public JobDetail getDetail() {
		return this.jobDetail;
	}
	

	public String getExecutionSource() {
		return executionSource;
	}

	public void setExecutionSource(String executionSource) {
		this.executionSource = executionSource;
	}

	public String getChannelData() {
		return channelData;
	}

	public void setChannelData(String channelData) {
		this.channelData = channelData;
	}

	public Map<String, DataFrame> getXcom() {
		return xcom;
	}

	public void setXcom(Map<String, DataFrame> xcom) {
		this.xcom = xcom;
	}

	public String getDagname() {
		return dagname;
	}

	public void setDagname(String dagname) {
		this.dagname = dagname;
	}

	
}
