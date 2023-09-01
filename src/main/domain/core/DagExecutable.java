package main.domain.core;

import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.support.WebApplicationContextUtils;
import main.application.ports.output.SchedulerRepositoryOutputPort;
import main.domain.annotations.Dag;
import main.domain.annotations.Operator;
import main.domain.enums.OperatorStatus;
import main.domain.exceptions.DomainException;
import main.infra.adapters.confs.InMemoryLoggerAppender;



//@Component
public class DagExecutable implements Job,JobListener {
	
	private static Logger log = Logger.getLogger(DagExecutable.class);
	
	protected class DagNode {
		private Class<?> operator;
		private String name;
		private Properties args;
		private Properties optionals;
		DagNode(String name,Class<?> operator,Properties args,Properties optionals){
			this.name = name;
			this.operator = operator;
			this.args = args;
			this.optionals = optionals;
		}
		public Class<?> getOperator() {
			return operator;
		}
		public void setOperator(Class<?> operator) {
			this.operator = operator;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Properties getArgs() {
			return args;
		}
		public void setArgs(Properties args) {
			this.args = args;
		}
		public Properties getOptionals() {
			return optionals;
		}
		public void setOptionals(Properties optionals) {
			this.optionals = optionals;
		}
	}
	private Boolean isRunning = true;
	private String dagname = "";
	private String eventname = "";
	private SchedulerRepositoryOutputPort repo;
	private Map<String,DagNode> nodeList = new HashMap<>();
	private Map<String,OperatorStatus> constraints = new HashMap<>();
	protected Graph<DagNode, DefaultEdge> g;
		
	protected JobDetail jobDetail;
	
	public DagExecutable() {
		this.g = new DirectedAcyclicGraph<>(DefaultEdge.class);
		var context = ContextLoader.getCurrentWebApplicationContext();
		if(context != null) {
			var servletctx = context.getServletContext();
			if(servletctx != null) {
				ApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(servletctx);
				if(springContext != null)
					repo = (SchedulerRepositoryOutputPort) springContext.getBean("schedulerRepository");	
			}
		}
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
		Map<String,OperatorStatus> status = new HashMap<>();
		Date evalDt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String evalstring = this.generateRandomString(12)+"_"+sdf.format(evalDt);
		
		var fa = this.createDagMemoryAppender(evalstring);
		JSONObject xcom = new JSONObject();
		Logger logdag = Logger.getLogger(evalstring);
		logdag.setLevel(Level.DEBUG);
		logdag.debug("executing dag::"+this.dagname);
		repo.setLog(evalstring,dagname, fa.getResult(),null,status);
		BreadthFirstIterator breadthFirstIterator  = new BreadthFirstIterator<>(g);
		while (breadthFirstIterator.hasNext()) {
			
			DagNode node = (DagNode) breadthFirstIterator.next();
			status.put(node.name, OperatorStatus.EXECUTING);
			logdag.debug("executing node::"+node.name);
			var statusToBe = this.constraints.get(node.name);
			if(statusToBe != null) {
				logdag.debug("constraint node::"+statusToBe.toString());	
			} else {
				logdag.debug("no constraint");
			}
			repo.setLog(evalstring,dagname, fa.getResult(),null,status);
			Class<?> clazz = node.operator;
			ExecutorService executorService = Executors.newSingleThreadExecutor();
			Future<?> future = executorService.submit(() -> {
			    try {
					this.instanciateEvaluate(evalstring, clazz, node, xcom, statusToBe, logdag, status, fa);
				} catch (JobExecutionException e) {
					logdag.error(e);
				}
			});
			while (!future.isDone()) {
			    try {
			    	repo.setLog(evalstring,dagname, fa.getResult(),null,status);
			    	Thread.sleep(500);	
				} catch (Exception e) {
					throw new JobExecutionException(e.getMessage());
				}
			}
		}
		return this.setLogEvaluate(evalstring,fa, xcom, status);
	}
	private void instanciateEvaluate(String evalkey,Class<?> clazz,DagNode node,JSONObject xcom,OperatorStatus statusToBe,Logger logdag,Map<String,OperatorStatus> status,InMemoryLoggerAppender fa) throws JobExecutionException {
		try {
			OperatorStage op = (OperatorStage) clazz.getDeclaredConstructor().newInstance();
			op.setArgs(node.args);
			op.setXcom(xcom);
			op.setName(node.name);
			op.setOptionals(node.optionals);
			Callable<?> instance  = (Callable<?>) op; 
			var result = instance.call();
			xcom.put(node.name , result );
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
				status.put(node.name, OperatorStatus.ERROR);
				logdag.error(e);
				Logger.getRootLogger().removeAppender(fa);
				try {
					String locatedAt = repo.createInternalStatus(xcom);
					repo.setLog(evalkey,dagname, fa.getResult(),locatedAt,status);	
				} catch (Exception e2) {
					log.error(e2);
				}
				throw new JobExecutionException(e);	
			}
		}
	}
	private OperatorStatus setLogEvaluate(String evaluatekey,InMemoryLoggerAppender fa,JSONObject xcom,Map<String,OperatorStatus> status) throws JobExecutionException {
		try {
			fa.close();
			Logger.getRootLogger().removeAppender(fa);
			String locatedAt = repo.createInternalStatus(xcom);
			repo.setLog(evaluatekey,dagname, fa.getResult(),locatedAt,status);
			return OperatorStatus.OK;	
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}
	private InMemoryLoggerAppender createDagMemoryAppender(String name) {
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
			throw new DomainException(e.getMessage());
		}
	}
	protected void addOperator(String name,Class<?> operator,Properties args) throws DomainException {
		try {
			this.addOperator(name, operator, args , new Properties());	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	
	protected void addOperator(String name,Class<?> operator,String propertyKey) throws DomainException {
		try {
			Properties props = this.getDagProperties(propertyKey);
			Properties propsOpt = new Properties();
			this.addOperator(name, operator, props, propsOpt);	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	
	protected void addOperator(String name,Class<?> operator,String propertyKey,String optionalsKey) throws DomainException {
		try {
			Properties props = this.getDagProperties(propertyKey);
			Properties propsOpt = this.getDagProperties(optionalsKey);
			this.addOperator(name, operator, props, propsOpt);	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	
	protected void addOperator(String name,Class<?> operator,Properties args,Properties optionals) throws DomainException {
		Operator annotation = operator.getAnnotation(Operator.class);
		String[] argsarr = annotation.args();
		for (int i = 0; i < argsarr.length; i++) {
			String string = argsarr[i];
			if(!args.containsKey(string)) {
				throw new DomainException(string + "not found");
			}
		}
		var node = new DagNode(name,operator,args,optionals);
		this.nodeList.put(name, node);
		this.g.addVertex(node);
	}
	
	protected void addDependency(String name1, String name2, String status) {
		this.addDependency(name1, name2, OperatorStatus.valueOf(status));
	}
	protected void addDependency(String name1, String name2, OperatorStatus status) {
		var node1 = this.nodeList.get(name1);
		var node2 = this.nodeList.get(name2);
		this.constraints.put(name1, status);
		try {
			this.g.addEdge(node1, node2);
		} catch (Exception e) {
			log.error(e);
		}	
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
				log.error(e);
			}	
		}
	}

	
	
	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		log.debug("jobExecutionVetoed");
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		if(this.eventname.equals("onEnd")) {
			try {
				this.evaluate();
			} catch (JobExecutionException e) {
				log.error(e);
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
			throw new DomainException(e.getMessage());
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
	private String generateRandomString(Integer targetStringLength) {
		int leftLimit = 97; // letter 'a'
	    int rightLimit = 122; // letter 'z'
	    Random random = new Random();
	    StringBuilder buffer = new StringBuilder(targetStringLength);
	    for (int i = 0; i < targetStringLength; i++) {
	        int randomLimitedInt = leftLimit + (int) 
	          (random.nextFloat() * (rightLimit - leftLimit + 1));
	        buffer.append((char) randomLimitedInt);
	    }
	    return buffer.toString();
	}
}
