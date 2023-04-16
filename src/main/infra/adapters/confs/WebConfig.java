package main.infra.adapters.confs;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import org.quartz.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import main.application.ports.input.GetDefaultJobsUseCase;


@EnableWebMvc
@Configuration
@ComponentScan(basePackages = { "main" })
@ImportResource("classpath:properties-config.xml")
public class WebConfig implements WebMvcConfigurer {

	@Value( "${org.quartz.dataSource.quartzDS.URL}" )
	private String db_host;
	
	@Value( "${org.quartz.dataSource.quartzDS.driver}" )
	private String db_driver;
	
	@Value( "${org.quartz.dataSource.quartzDS.user}" )
	private String db_user;
	
	@Value( "${org.quartz.dataSource.quartzDS.password}" )
	private String db_pass;
	
	@Value("${param.hibernate.dialect}")
	private String db_dialect;
	
	@Value("${param.flyway.migrations}")
	private String db_migrations;
	
	
	
	private final static Logger logger = Logger.getLogger(WebConfig.class);
	private List<Job> defaultjobs;

	@Autowired
	QuartzConfig quartz;
	
	@Autowired 
	GetDefaultJobsUseCase defaults;

	@SuppressWarnings("unchecked")
	@EventListener(ContextRefreshedEvent.class)
	public void contextRefreshedEvent(ContextRefreshedEvent context) {
		if(!context.getApplicationContext().getDisplayName().equals("Root WebApplicationContext")) {
			try {
				this.defaultjobs = (List<Job>)(Object) this.defaults.getDefaultJobs();
				quartz.init(defaultjobs);
				logger.debug("starting QUARTZ");
				for (Iterator<Job> iterator = defaultjobs.iterator(); iterator.hasNext();) {
					 var job = iterator.next();
					 quartz.executeInmediate(job);	
				}
			} catch (Exception e) {
				logger.error(e);
				e.printStackTrace();
			}	
		}
	}
	@EventListener(ContextClosedEvent.class)
	public void contextCleanupEvent() {
		try {
			logger.debug("stopping QUARTZ");
			for (Job jobType : this.defaultjobs) {
				quartz.deactivateJob(jobType);	
			}
			quartz.stop();
			quartz.getScheduler().clear();
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	@Bean
	@Autowired
	public HibernateTransactionManager transactionManager(SessionFactory s) {
	   HibernateTransactionManager txManager = new HibernateTransactionManager();
	   txManager.setSessionFactory(s);
	   return txManager;
	}
	@Bean
	public DataSource dataSource() {
		BasicDataSource  dataSource = new BasicDataSource();
		dataSource.setDriverClassName(this.db_driver);
		if(System.getenv("APP_JDBC_USER") != null) {
			dataSource.setUsername(System.getenv("APP_JDBC_USER"));
		} else {
			dataSource.setUsername(this.db_user);
		}
		if(System.getenv("APP_JDBC_USER") != null) {
			dataSource.setPassword(System.getenv("APP_JDBC_PASSWORD"));
		} else {
			dataSource.setPassword(this.db_pass);
		}
		if(System.getenv("APP_JDBC_URL") != null) {
			dataSource.setUrl(System.getenv("APP_JDBC_URL"));
		} else {
			dataSource.setUrl(this.db_host);
		}
	    return dataSource;
	}
	@Bean
	public LocalSessionFactoryBean sessionFactory() {
	    LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
	    sessionFactory.setDataSource(dataSource());
	    sessionFactory.setPackagesToScan(new String[] { "main" });
	    sessionFactory.setHibernateProperties(hibernateProperties());
	    return sessionFactory;
	}
	private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", db_dialect);
        properties.put("hibernate.show_sql", false);
        return properties;        
    }
	@Bean(initMethod = "migrate")
	public Flyway flyway() {
	    Flyway flyway = new Flyway();
	    flyway.setDataSource(dataSource());
	    flyway.setBaselineOnMigrate(true);
	    return flyway;
	}
	
	
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
          .addResourceHandler("/cli/**")
          .addResourceLocations("/WEB-INF/cli/");	
    }
}