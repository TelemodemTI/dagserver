package main.cl.dagserver.infra.adapters.confs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.nhl.dflib.DataFrame;
import main.cl.dagserver.domain.core.DagExecutable;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.PropertyParameterDTO;
import main.cl.dagserver.infra.adapters.output.repositories.SchedulerRepository;
import main.cl.dagserver.domain.annotations.Dag;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.Matcher;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.matchers.KeyMatcher;
import org.quartz.utils.Key;

@Component
public class QuartzConfig {
	
	
	private SchedulerRepository repo;
	
	
	private static final String APP_JDBC_URL = "APP_JDBC_URL";
	private static final String APP_JDBC_USER = "APP_JDBC_USER";
	private static final String APP_JDBC_DRIVER = "APP_JDBC_DRIVER";
	private static final String APP_JDBC_PASSWORD = "APP_JDBC_PASSWORD";
	
	private static final String VALUE = "value.";
	
	private static final String PREFIX_JOB_DB = "";
	private Scheduler scheduler;
	
	@Autowired
	public QuartzConfig(SchedulerRepository repo) {
		this.repo = repo;
	}
	
	public Scheduler getScheduler() {
		return scheduler;
	}
	@SuppressWarnings("static-access")
	public void scheduleOne(Job jobType,String key,Map<?,?> map,Date dateRun) throws SchedulerException{
		String jobName = key + "_db_" + dateRun.getTime();
		JobKey jobKey = new JobKey(jobName);
		TriggerKey triggerKey = new TriggerKey(Key.DEFAULT_GROUP + jobName);
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).startAt(dateRun).build();
		JobBuilder jobBuilder = JobBuilder.newJob(jobType.getClass() ).withIdentity(jobKey);
		jobBuilder.withDescription(jobName);
		JobDetail job = jobBuilder.build();
		Map<String,Object> params = new HashMap<>();
		params.put("item", map.get(key));
		params.put("key", key);
		params.put("map", map);
		job.getJobDataMap().putAll(params);
		this.scheduler.scheduleJob(job, trigger);
	}
	@SuppressWarnings("static-access")
	public void scheduleRecurrente(Job jobType,String key,Map<?,?> map,String cronExpr) throws DomainException{
		try {
			String jobName = key + "_cb_" + new Date().getTime();
			JobKey jobKey = new JobKey(jobName);
			
			TriggerKey triggerKey = new TriggerKey(Key.DEFAULT_GROUP + jobName);
			Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(CronScheduleBuilder.cronSchedule(cronExpr)).build();
			JobBuilder jobBuilder = JobBuilder.newJob(jobType.getClass()).withIdentity(jobKey);
			jobBuilder.withDescription(jobName);
			JobDetail job = jobBuilder.build();
			Map<String,Object> params = new HashMap<>();
			params.put("item", map.get(key));
			params.put("cronExpr", cronExpr);
			params.put("key", key);
			params.put("map", map);
			job.getJobDataMap().putAll(params);
			this.scheduler.scheduleJob(job, trigger);	
		} catch (Exception e) {
			throw new DomainException(e);
		}
		
	}
	
	@SuppressWarnings("static-access")
	public void deleteScheduled(String jobName) throws SchedulerException{
		JobKey jobKey = new JobKey(jobName);
		if (this.scheduler.checkExists(jobKey)) {
			this.scheduler.deleteJob(jobKey);
		}
	}
	
	

	public CompletableFuture<Map<String,DataFrame>> executeInmediate(DagExecutable dag) throws DomainException {
	    Job jobType = (Job) dag;
	    CompletableFuture<Map<String,DataFrame>> future = new CompletableFuture<>();

	    try {
	        // Crear el trigger para ejecutar el job inmediatamente
	        Trigger trigger = TriggerBuilder.newTrigger().startNow().build();

	        // Crear el JobDetail, incluyendo la información que quieres pasar
	        JobDetail jobDetail = JobBuilder.newJob(jobType.getClass())
	                .withIdentity(jobType.getClass().getName())
	                .build();

	        // Pasar los datos al JobDataMap
	        jobDetail.getJobDataMap().put("channel", dag.getExecutionSource());
	        jobDetail.getJobDataMap().put("channelData", dag.getChannelData());

	        // Agregar el listener para el job actual
	        JobListener jobListener = new JobListener() {
	            @Override
	            public String getName() {
	                return "JobCompletionListener";
	            }

	            @Override
	            public void jobToBeExecuted(JobExecutionContext context) {
	                
	            }

	            @Override
	            public void jobExecutionVetoed(JobExecutionContext context) {
	                future.completeExceptionally(new RuntimeException("Job vetado: " + context.getJobDetail().getKey()));
	            }

	            @Override
	            public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
	                if (jobException != null) {
	                    future.completeExceptionally(jobException);
	                } else {
	                    var dagexec = (DagExecutable) context.getJobInstance();
	                    future.complete(dagexec.getXcom());
	                }
	            }
	        };
	        this.scheduler.getListenerManager().addJobListener(jobListener, KeyMatcher.keyEquals(jobDetail.getKey()));
	        this.scheduler.scheduleJob(jobDetail, trigger);
	    } catch (Exception e) {
	        future.completeExceptionally(e);
	        throw new DomainException(e);
	    }
	    return future;
	}


	private String getRealCronExpr(String cronExpr) {
	    if (cronExpr.startsWith("${") && cronExpr.endsWith("}")) {
	        String key = cronExpr.substring(2, cronExpr.length() - 1); // Extrae la clave sin ${}
	        Environment env = ApplicationContextUtils.getApplicationContext().getBean(Environment.class);
	        return env.getProperty(key, cronExpr); 
	    }
	    return cronExpr;
	}
	
	public void activateJob(Job jobType,String group) throws SchedulerException {	
		Dag type = jobType.getClass().getAnnotation(Dag.class); 
		String jobName = PREFIX_JOB_DB + type.name();
		JobKey jobKey = new JobKey(jobName,group);
		TriggerKey triggerKey = new TriggerKey(Key.DEFAULT_GROUP + jobName);
		String rcron = type.cronExpr();
		Trigger trigger = this.createOrGetTrigger(triggerKey,this.getRealCronExpr(rcron));
		this.createOrUpdateJob(jobKey, jobType.getClass(), trigger,type.cronExpr());
	}
	@SuppressWarnings("static-access")
	public void deactivateJob(Job jobType) throws SchedulerException {
		Dag type = jobType.getClass().getAnnotation(Dag.class); 
		String jobName = PREFIX_JOB_DB + type.name();
		JobKey jobKey = new JobKey(jobName,type.group());
		if (this.scheduler.checkExists(jobKey)) {
			this.scheduler.deleteJob(jobKey);
		}
	}
	@SuppressWarnings("static-access")
	public void stop() throws SchedulerException {
		this.scheduler.shutdown(false);
	}
	@SuppressWarnings("static-access")
	public void stop(boolean waitForJobs) throws SchedulerException {		
		this.scheduler.shutdown(waitForJobs);
		this.scheduler = null;
	}
	@SuppressWarnings("static-access")
	private Trigger createOrGetTrigger(TriggerKey key, String cronExpression) throws SchedulerException {		
		Trigger trigger = null;
		if (this.scheduler.checkExists(key)) {
			trigger = this.scheduler.getTrigger(key);
		} else {
			trigger = TriggerBuilder.newTrigger().withIdentity(key)
					.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();
		}
		return trigger;
	}
	@SuppressWarnings("static-access")
	private void createOrUpdateJob(JobKey jobKey, Class<? extends org.quartz.Job> clazz, Trigger trigger,String cronExpr)
			throws SchedulerException {
		JobBuilder jobBuilder = JobBuilder.newJob(clazz).withIdentity(jobKey);		
		if (!this.scheduler.checkExists(jobKey)) {
			JobDetail detail = jobBuilder.build();
			detail.getJobDataMap().put("cronExpr", cronExpr);
			this.scheduler.scheduleJob(detail, trigger);
		} else {
			List<? extends Trigger> triggers = this.scheduler.getTriggersOfJob(jobKey);
			if (triggers.size() == 1) {
				this.scheduler.rescheduleJob(triggers.get(0).getKey(), trigger);
				return;
			}
			this.scheduler.deleteJob(jobKey);
			this.scheduler.scheduleJob(jobBuilder.build(), trigger);
		}
	}
	@SuppressWarnings("static-access")
	public void init(List<Job> defaultjobs) throws DomainException {
		try {
			Properties p = this.getQuartzProperties();
			StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();
			schedulerFactory.initialize(p);
			this.scheduler = schedulerFactory.getScheduler();
			if (this.scheduler == null)
				throw new SchedulerException("QUARTZ not initialized!.");
			for (Job jobType : defaultjobs) {
				Dag type = jobType.getClass().getAnnotation(Dag.class); 
				DagExecutable executable = (DagExecutable) jobType;
				executable.setName(type.name());
				if(!type.cronExpr().isEmpty()){
					this.activateJob(executable,type.group());	
				} else {
					this.configureListener(type,executable);
				}
			}
			this.scheduler.start();	
		} catch (Exception e) {
			throw new DomainException(e);
		}
		
	}
	public void configureListener(Dag annotation,DagExecutable executable) throws SchedulerException {
		String eventname = annotation.onEnd().equals("") ? "onStart" : "onEnd";
		executable.setEventname(eventname);
		executable.setName(annotation.name());
		JobListener listener = executable;
		String target = annotation.target();
		String matcherkey = annotation.onEnd().equals("") ? annotation.onStart() : annotation.onEnd();
		var list = new ArrayList<Matcher<JobKey>>();
		if(target.equals("DAG")) {
			JobKey key1 = new JobKey(matcherkey, annotation.group());
			list.add(KeyMatcher.keyEquals(key1));	
		} else {
			list.add(GroupMatcher.jobGroupEquals(matcherkey));	
		}	
		this.scheduler.getListenerManager().addJobListener(listener,list);
		this.repo.addEventListener(listener.getName(), annotation.onStart(), annotation.onEnd(), annotation.group());
	}
	public void removeListener(Dag annotation) throws SchedulerException {
		this.scheduler.getListenerManager().removeJobListener(annotation.name());
		repo.removeListener(annotation.name());
	}
	public List<Map<String,Object>> listScheduled() throws SchedulerException {
		var arr = new ArrayList<Map<String,Object>>();
		for (String groupName : scheduler.getJobGroupNames()) {
			 for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
				 var map = new HashMap<String,Object>(); 
				 map.put("jobname", jobKey.getName());
				 map.put("jobgroup", jobKey.getGroup());
				 @SuppressWarnings("unchecked")
				List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
				 map.put("nextFireAt", triggers.get(0).getNextFireTime());
				 map.put("eventTrigger", "");
				 arr.add(map);
			  }
		}
		return arr;
	}
	private Properties getQuartzProperties() throws IOException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();           
		InputStream stream = loader.getResourceAsStream("quartz.properties");
		Properties p = new Properties();
		p.load(stream);
		if(System.getenv(APP_JDBC_DRIVER) != null) {
			p.setProperty("org.quartz.dataSource.quartzDS.driver", System.getenv(APP_JDBC_DRIVER));
		}
		if(System.getenv(APP_JDBC_URL) != null) {
			p.setProperty("org.quartz.dataSource.quartzDS.URL", System.getenv(APP_JDBC_URL));	
		}
		if(System.getenv(APP_JDBC_USER) != null) {
			p.setProperty("org.quartz.dataSource.quartzDS.user", System.getenv(APP_JDBC_USER));
		}
		if(System.getenv(APP_JDBC_PASSWORD) != null) {
			p.setProperty("org.quartz.dataSource.quartzDS.password", System.getenv(APP_JDBC_PASSWORD));
		}
		return p;
	}
	public void validate(String jarname,Map<String, Properties> analizeJarProperties) {
		for (Entry<String, Properties> entry : analizeJarProperties.entrySet() ) {
			Properties properties = analizeJarProperties.get(entry.getKey());
	        repo.insertIfNotExists(jarname,entry.getKey(),properties);
		}
	}
	public void propertiesToRepo(Properties prop) throws DomainException {
		List<String> keys = new ArrayList<>();
		for (String key : prop.stringPropertyNames()) {
		    if(key.startsWith(VALUE)) {
		    	String real = key.replace(VALUE, "");
		    	if(!keys.contains(real)) {
		    		keys.add(real);
		    	}
		    }
		}
		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String key = iterator.next();
			String descr = prop.getProperty("desc."+key);
			String group = prop.getProperty("group."+key);
			String value = prop.getProperty(VALUE+key);
			var props = repo.getProperties(group);
			boolean found = false;
	        for (PropertyParameterDTO existingProperty : props) {
	            if (existingProperty.getName().equals(key)) {
	                found = true;
	                break;
	            }
	        }
	        if (!found) {
	        	repo.setProperty(key, descr, value, group);	
	        }
		}
	}
}