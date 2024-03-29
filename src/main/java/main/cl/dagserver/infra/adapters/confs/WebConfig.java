package main.cl.dagserver.infra.adapters.confs;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.flywaydb.core.Flyway;
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
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
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

	private static final String APP_JDBC_URL = "APP_JDBC_URL";
	private static final String APP_JDBC_USER = "APP_JDBC_USER";
	private static final String APP_JDBC_DRIVER = "APP_JDBC_DRIVER";
	private static final String APP_MIGRATION_JDBC_TYPE = "APP_MIGRATION_JDBC_TYPE";
	private static final String APP_JDBC_PASSWORD = "APP_JDBC_PASSWORD";
	private static final String APP_HIBERNATE_DIALECT = "APP_HIBERNATE_DIALECT"; 

	@Value( "${org.quartz.dataSource.quartzDS.URL}" )
	private String dbHost;
	
	@Value( "${org.quartz.dataSource.quartzDS.driver}" )
	private String dbDriver;
	
	@Value( "${org.quartz.dataSource.quartzDS.user}" )
	private String dbUser;
	
	@Value( "${org.quartz.dataSource.quartzDS.password}" )
	private String dbPass;
	
	@Value("${param.hibernate.dialect}")
	private String dbDialect;
	
	@Value("${param.flyway.migrations}")
	private String dbMigrations;
	
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
	public DataSource dataSource() {
		BasicDataSource  dataSource = new BasicDataSource();
		if(System.getenv(APP_JDBC_DRIVER) != null) {
			dataSource.setDriverClassName(System.getenv(APP_JDBC_DRIVER));
		} else {
			dataSource.setDriverClassName(dbDriver);
		}
		if(System.getenv(APP_JDBC_USER) != null) {
			dataSource.setUsername(System.getenv(APP_JDBC_USER));
		} else {
			dataSource.setUsername(this.dbUser);
		}
		if(System.getenv(APP_JDBC_USER) != null) {
			dataSource.setPassword(System.getenv(APP_JDBC_PASSWORD).trim());
		} else {
			dataSource.setPassword(this.dbPass);
		}
		if(System.getenv(APP_JDBC_URL) != null) {
			dataSource.setUrl(System.getenv(APP_JDBC_URL));
		} else {
			dataSource.setUrl(this.dbHost);
		}
	    return dataSource;
	}
	@Bean
	public LocalSessionFactoryBean sessionFactory() {
	    LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
	    sessionFactory.setDataSource(dataSource());
	    sessionFactory.setPackagesToScan("main");
	    sessionFactory.setHibernateProperties(hibernateProperties());
	    return sessionFactory;
	}
	private Properties hibernateProperties() {
        Properties properties = new Properties();
        if(System.getenv(APP_HIBERNATE_DIALECT) != null) {
        	properties.put("hibernate.dialect", System.getenv(APP_HIBERNATE_DIALECT));
		} else {
			properties.put("hibernate.dialect",dbDialect );
		}
        properties.put("hibernate.show_sql", false);
        properties.put("hibernate.temp.use_jdbc_metadata_defaults", false);
        return properties;        
    }
	@Bean(initMethod = "migrate")
	public Flyway flyway() {
	    Flyway flyway = new Flyway();
	    flyway.setDataSource(dataSource());
	    flyway.setBaselineOnMigrate(true);
	    if(System.getenv(APP_MIGRATION_JDBC_TYPE) != null) {
	    	flyway.setLocations(System.getenv(APP_MIGRATION_JDBC_TYPE));
		} else {
			flyway.setLocations(dbMigrations);
		}
	    return flyway;
	}
	
	
}