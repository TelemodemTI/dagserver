package main.cl.dagserver.infra.adapters.confs;

import java.util.Iterator;
import java.util.List;
import org.quartz.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import main.cl.dagserver.application.ports.input.GetDefaultJobsUseCase;
import main.cl.dagserver.domain.annotations.Dag;
import main.cl.dagserver.domain.core.DagExecutable;
import main.cl.dagserver.domain.core.ExceptionEventLog;
import main.cl.dagserver.domain.exceptions.DomainException;



@Configuration
@ComponentScan(basePackages = { "main" })
@ImportResource("classpath:properties-config.xml")
public class WebConfig implements WebMvcConfigurer {

	@Value( "${org.quartz.dataSource.quartzDS.URL}" )
	private String dbHost;
	
	@Value( "${org.quartz.dataSource.quartzDS.driver}" )
	private String dbDriver;
	
	@Value( "${org.quartz.dataSource.quartzDS.user}" )
	private String dbUser;
	
	@Value( "${org.quartz.dataSource.quartzDS.password}" )
	private String dbPass;

	
	private List<Job> defaultjobs;

	
	private QuartzConfig quartz;
	private GetDefaultJobsUseCase defaults;
    private ApplicationEventPublisher eventPublisher;
	
	@Autowired
	public WebConfig(ApplicationEventPublisher eventPublisher, QuartzConfig quartz, GetDefaultJobsUseCase defaults) {
	    this.eventPublisher = eventPublisher;
	    this.quartz = quartz;
	    this.defaults = defaults;
	}
	
	@SuppressWarnings("unchecked")
	@EventListener(ContextRefreshedEvent.class)
	public void contextRefreshedEvent(ContextRefreshedEvent context) {
		if(!context.getApplicationContext().getDisplayName().equals("Root WebApplicationContext")) {
			try {
				this.defaultjobs = (List<Job>)(Object) this.defaults.getDefaultJobs();
				quartz.init(defaultjobs);
				for (Iterator<Job> iterator = defaultjobs.iterator(); iterator.hasNext();) {
					 var job = iterator.next();
					 Dag type = job.getClass().getAnnotation(Dag.class);
					 if(!type.cronExpr().isEmpty()) {
						 quartz.executeInmediate((DagExecutable) job);	 
					 } else {
						 quartz.configureListener(type, (DagExecutable) job);
					 }
					 	
				}
			} catch (Exception e) {
				eventPublisher.publishEvent(new ExceptionEventLog(this, new DomainException(e), "contextRefreshedEvent"));
			}	
		}
	}
	@EventListener(ContextClosedEvent.class)
	public void contextCleanupEvent() {
		try {
			for (Job jobType : this.defaultjobs) {
				quartz.deactivateJob(jobType);	
			}
			quartz.stop();
			quartz.getScheduler().clear();
		} catch (Exception e) {
			eventPublisher.publishEvent(new ExceptionEventLog(this, new DomainException(e), "contextCleanupEvent"));
		}
	}
	@Bean
	public ApplicationEventPublisher eventPublisher() {
		return eventPublisher;
	}
}