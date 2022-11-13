package main.infra.adapters.confs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import main.domain.core.DagExecutable;
import main.domain.repositories.SchedulerRepository;
import main.domain.annotations.Dag;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
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

@Component
public class QuartzConfig {
	
	@Autowired
	SchedulerRepository repo;
	
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(QuartzConfig.class);
	
	//private static final String CRON_STATEMENT = "0 0/1 * * * ?";
	private static final String FILE_CONFIG_QUARTZ = "quartz.properties";
	private static final String PREFIX_JOB_DB = "";
	private static Scheduler scheduler;
	
	public Scheduler getScheduler() {
		return scheduler;
	}
	@SuppressWarnings("static-access")
	public void scheduleOne(Job jobType,String key,Map<?,?> map,Date dateRun) throws SchedulerException{
		String jobName = key + "_db_" + dateRun.getTime();
		JobKey jobKey = new JobKey(jobName);
		TriggerKey triggerKey = new TriggerKey(TriggerKey.DEFAULT_GROUP + jobName);
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).startAt(dateRun).build();
		JobBuilder jobBuilder = JobBuilder.newJob(jobType.getClass() ).withIdentity(jobKey);
		jobBuilder.withDescription(jobName);
		JobDetail job = jobBuilder.build();
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("item", map.get(key));
		params.put("key", key);
		params.put("map", map);
		job.getJobDataMap().putAll(params);
		this.scheduler.scheduleJob(job, trigger);
	}
	@SuppressWarnings("static-access")
	public void scheduleRecurrente(Job jobType,String key,Map<?,?> map,String cronExpr) throws Exception{
		String jobName = key + "_cb_" + new Date().getTime();
		JobKey jobKey = new JobKey(jobName);
		TriggerKey triggerKey = new TriggerKey(TriggerKey.DEFAULT_GROUP + jobName);
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(CronScheduleBuilder.cronSchedule(cronExpr)).build();
		JobBuilder jobBuilder = JobBuilder.newJob(jobType.getClass()).withIdentity(jobKey);
		jobBuilder.withDescription(jobName);
		JobDetail job = jobBuilder.build();
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("item", map.get(key));
		params.put("cronExpr", cronExpr);
		params.put("key", key);
		params.put("map", map);
		job.getJobDataMap().putAll(params);
		this.scheduler.scheduleJob(job, trigger);
	}
	
	@SuppressWarnings("static-access")
	public void deleteScheduled(String jobName) throws SchedulerException{
		JobKey jobKey = new JobKey(jobName);
		if (this.scheduler.checkExists(jobKey)) {
			this.scheduler.deleteJob(jobKey);
		}
	}
	
	
	public void activateJob(Job jobType,String group) throws SchedulerException {	
		Dag type = jobType.getClass().getAnnotation(Dag.class); 
		String jobName = PREFIX_JOB_DB + type.name();
		JobKey jobKey = new JobKey(jobName,group);
		TriggerKey triggerKey = new TriggerKey(TriggerKey.DEFAULT_GROUP + jobName);
		Trigger trigger = this.createOrGetTrigger(triggerKey,type.cronExpr());
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
	public void init(List<Job> defaultjobs) throws SchedulerException {
		StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();
		schedulerFactory.initialize(FILE_CONFIG_QUARTZ);
		this.scheduler = schedulerFactory.getScheduler();
		if (this.scheduler == null)
			throw new SchedulerException("QUARTZ not initialized!.");
		for (Job jobType : defaultjobs) {
			Dag type = jobType.getClass().getAnnotation(Dag.class); 
			DagExecutable executable = (DagExecutable) jobType;
			executable.setName(type.name());
			if(!type.cronExpr().equals("")){
				this.activateJob(executable,type.group());	
			} else {
				this.configureListener(type,executable);
			}
		}
		this.scheduler.start();
	}
	public void configureListener(Dag annotation,DagExecutable executable) throws SchedulerException {
		String eventname = annotation.onEnd().equals("") ? "onStart" : "onEnd";
		executable.setEventname(eventname);
		JobListener listener = (JobListener) executable;
		String jobkey = annotation.onEnd().equals("") ? annotation.onStart() : annotation.onEnd();
		JobKey jobKey1 = new JobKey(jobkey, annotation.group());
		var list = new ArrayList<Matcher<JobKey>>();
		list.add(KeyMatcher.keyEquals(jobKey1));
		this.scheduler.getListenerManager().addJobListener(listener,list);
		this.repo.addEventListener(listener.getName(), annotation.onStart(), annotation.onEnd(), annotation.group());
	}
	public void removeListener(Dag annotation,DagExecutable executable) throws SchedulerException {
		this.scheduler.getListenerManager().removeJobListener(annotation.name());
		var eventList = repo.getEventListeners(annotation.name()).get(0);  
		repo.removeListener(annotation.name());
	}
	public List<Map<String,Object>> listScheduled() throws SchedulerException {
		var arr = new ArrayList<Map<String,Object>>();
		for (String groupName : scheduler.getJobGroupNames()) {
			 for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
				 var map = new HashMap<String,Object>(); 
				 map.put("jobname", jobKey.getName());
				 map.put("jobgroup", jobKey.getGroup());
				 List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
				 map.put("nextFireAt", triggers.get(0).getNextFireTime());
				 map.put("eventTrigger", "");
				 arr.add(map);
			  }
		}
		return arr;
	}
	
}