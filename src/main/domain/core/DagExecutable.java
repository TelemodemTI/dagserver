package main.domain.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
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
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.WebApplicationContextUtils;

import main.domain.annotations.Dag;
import main.domain.annotations.Operator;
import main.domain.enums.OperatorStatus;
import main.infra.adapters.confs.InMemoryLoggerAppender;
import main.infra.adapters.input.graphql.types.OperatorStage;
import main.infra.adapters.output.repositories.SchedulerRepository;


//@Component
public class DagExecutable implements Job,JobListener {
	
	private static Logger log = Logger.getLogger(DagExecutable.class);
	protected class DagNode {
		public Class<?> operator;
		public String name;
		public Properties args;
		public Properties optionals;
		DagNode(String name,Class<?> operator,Properties args,Properties optionals){
			this.name = name;
			this.operator = operator;
			this.args = args;
			this.optionals = optionals;
		}
	}
	private Boolean isRunning = true;
	
	private String dagname = "";
	private String eventname = "";
	private SchedulerRepository repo;
	private Map<String,DagNode> nodeList = new HashMap<>();
	private Map<String,OperatorStatus> constraints = new HashMap<>();
	protected Graph<DagNode, DefaultEdge> g;
	protected Map<String,Object> xcom = new HashMap<String,Object>();
	protected JobDetail jobDetail;
	
	public DagExecutable() {
		this.g = new DirectedAcyclicGraph<>(DefaultEdge.class);
		ApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(ContextLoaderListener.getCurrentWebApplicationContext().getServletContext());
		repo = (SchedulerRepository) springContext.getBean("schedulerRepository");
	}
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		jobDetail = context.getJobDetail();
		this.isRunning = true;
		Dag anno = this.getClass().getAnnotation(Dag.class);
		this.dagname = anno.name();
		var status = this.evaluate();
		this.isRunning = false;
		log.debug("outcode::" + status.toString());
	}
	

	
	@SuppressWarnings("rawtypes")
	private OperatorStatus evaluate() throws JobExecutionException {
		var fa = this.createDagMemoryAppender();
		Logger logdag = Logger.getLogger("DAG");
		logdag.setLevel(Level.DEBUG);
		logdag.debug("executing dag::"+this.dagname);
		Map<String,OperatorStatus> status = new HashMap<>();
		BreadthFirstIterator breadthFirstIterator  = new BreadthFirstIterator<>(g);
		while (breadthFirstIterator.hasNext()) {
			DagNode node = (DagNode) breadthFirstIterator.next();
			logdag.debug("executing node::"+node.name);
			var statusToBe = this.constraints.get(node.name);
			if(statusToBe != null) {
				logdag.debug("constraint node::"+statusToBe.toString());	
			} else {
				logdag.debug("no constraint");
			}
			Class<?> clazz = node.operator;
			try {
				OperatorStage op = (OperatorStage) clazz.getDeclaredConstructor().newInstance();
				op.setArgs(node.args);
				op.setXcom(xcom);
				op.setName(node.name);
				op.setOptionals(node.optionals);
				Callable<?> instance  = (Callable<?>) op; 
				var result = instance.call();
				this.xcom.put(node.name , result );
				if( (statusToBe == null) || (statusToBe == OperatorStatus.OK ) || (statusToBe == OperatorStatus.ANY)) {
					logdag.debug("::end execution::");
					status.put(node.name, OperatorStatus.OK);
				} else {
					status.put(node.name, OperatorStatus.ERROR);
					throw new JobExecutionException("constraint failed::"+node.name);	
				}
			} catch (Exception e) {
				if(statusToBe == OperatorStatus.ERROR) {
					status.put(node.name, OperatorStatus.ERROR);
					logdag.debug("result::"+e.getMessage());
				} else {
					status.put(node.name, OperatorStatus.OK);
					throw new JobExecutionException(e);	
				}
			}	
		}
		Logger.getRootLogger().removeAppender(fa);
		repo.setLog(dagname, fa.getResult(),this.xcom,status);
		return OperatorStatus.OK;
	}
	private InMemoryLoggerAppender createDagMemoryAppender() {
		InMemoryLoggerAppender fa = new InMemoryLoggerAppender();
		fa.setName("DAG");
		fa.setLayout(new PatternLayout("%d %-5p [%c{1}] %m%n"));
		fa.setThreshold(Level.DEBUG);
		fa.activateOptions();
		Logger.getRootLogger().addAppender(fa);
		return fa;
	}
	protected void addOperator(String name,Class<?> operator) throws Exception {
		this.addOperator(name, operator, new Properties() , new Properties());
	}
	protected void addOperator(String name,Class<?> operator,Properties args) throws Exception {
		this.addOperator(name, operator, args , new Properties());
	}
	protected void addOperator(String name,Class<?> operator,Properties args,Properties optionals) throws Exception {
		Operator annotation = operator.getAnnotation(Operator.class);
		String[] argsarr = annotation.args();
		for (int i = 0; i < argsarr.length; i++) {
			String string = argsarr[i];
			if(!args.containsKey(string)) {
				throw new Exception(string + "not found");
			}
		}
		var node = new DagNode(name,operator,args,optionals);
		this.nodeList.put(name, node);
		this.g.addVertex(node);
	}
	protected void addDependency(String name1, String name2, OperatorStatus status) {
		var node1 = this.nodeList.get(name1);
		var node2 = this.nodeList.get(name2);
		this.constraints.put(name1, status);
		try {
			this.g.addEdge(node1, node2);
		} catch (Exception e) {}	
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
				this.evaluate();
			} catch (JobExecutionException e) {
				e.printStackTrace();
			}	
		}
	}

	
	
	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {}

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		if(this.eventname.equals("onEnd")) {
			try {
				this.evaluate();
			} catch (JobExecutionException e) {
				e.printStackTrace();
			}	
		}
	}

	public String getEventname() {
		return eventname;
	}

	public void setEventname(String eventname) {
		this.eventname = eventname;
	}
	
	public List<List<String>> getDagGraph(){
		var info = new ArrayList<List<String>>();
		BreadthFirstIterator<DagNode, DefaultEdge> breadthFirstIterator  = new BreadthFirstIterator<>(g);
		Integer index = 1;
		while (breadthFirstIterator.hasNext()) {
			var detail = new ArrayList<String>();
			DagNode node = (DagNode) breadthFirstIterator.next();
			detail.add(node.name);
			detail.add(node.operator.getCanonicalName());
			JSONObject props = new JSONObject(node.args);
			JSONObject opts = new JSONObject(node.optionals);
			detail.add(props.toString());
			detail.add(opts.toString());
			info.add(detail);
			index++;
		}
		return info;
	}
	
	protected Properties getDagProperties(String string) throws Exception {
		return repo.getPropertiesFromDb(string);
	}

	
	public void setXcom(Map<String, Object> xcom) {
		this.xcom = xcom;
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

}
