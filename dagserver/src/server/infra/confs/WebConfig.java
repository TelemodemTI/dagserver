package server.infra.confs;

import java.util.ArrayList;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import org.quartz.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
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
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import server.application.dags.BackgroundSystemDag;
import server.application.dags.EventSystemDag;
import server.infra.quartz.QuartzConfig;



@EnableWebMvc
@Configuration
@ComponentScan(basePackages = { "server.*" })
@ImportResource("classpath:properties-config.xml")
public class WebConfig implements WebMvcConfigurer {

	@Value( "${org.quartz.dataSource.quartzDS.URL}" )
	private String db_host;
	
	@Value( "${org.quartz.dataSource.quartzDS.user}" )
	private String db_user;
	
	@Value( "${org.quartz.dataSource.quartzDS.password}" )
	private String db_pass;
	
	private final static Logger logger = Logger.getLogger(WebConfig.class);
	private ArrayList<Job> defaultjobs;
	private WSServer websocket;

	@Autowired
	QuartzConfig quartz;

	@EventListener(ContextRefreshedEvent.class)
	public void contextRefreshedEvent(ContextRefreshedEvent context) {
		if(!context.getApplicationContext().getDisplayName().equals("Root WebApplicationContext")) {
			try {
				this.defaultjobs = new ArrayList<Job>();
				this.defaultjobs.add(new BackgroundSystemDag());
				this.defaultjobs.add(new EventSystemDag());
				quartz.init(defaultjobs);
				logger.debug("starting QUARTZ");
				
				
	 			if(this.websocket == null) {
					websocket = new WSServer();
	 				websocket.start();
					logger.debug("starting Websocket");
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
	    dataSource.setDriverClassName("com.mysql.jdbc.Driver");
	    dataSource.setUsername(this.db_user);
	    dataSource.setPassword(this.db_pass);
	    dataSource.setUrl(this.db_host);
	    return dataSource;
	}
	@Bean
	public LocalSessionFactoryBean sessionFactory() {
	    LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
	    sessionFactory.setDataSource(dataSource());
	    sessionFactory.setPackagesToScan(new String[] { "server" });
	    sessionFactory.setHibernateProperties(hibernateProperties());
	    return sessionFactory;
	}
	private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLInnoDBDialect");
        properties.put("hibernate.show_sql", false);
        return properties;        
    }
	@Bean(initMethod = "migrate")
	public Flyway flyway() {
	    Flyway flyway = new Flyway();
	    flyway.setDataSource(dataSource());
	    flyway.setBaselineOnMigrate(true);
	    flyway.setLocations("classpath:/server/migrations");
	    return flyway;
	}
}