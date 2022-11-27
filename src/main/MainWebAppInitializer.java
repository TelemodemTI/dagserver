package main;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;



@EnableWebMvc
@Configuration
public class MainWebAppInitializer implements WebApplicationInitializer {
  

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		AnnotationConfigWebApplicationContext root = new AnnotationConfigWebApplicationContext();
		root.scan("main");
		servletContext.addListener(new ContextLoaderListener(root));
		ServletRegistration.Dynamic appServlet = servletContext.addServlet("mvcqw", new DispatcherServlet(new GenericWebApplicationContext()));
		
		appServlet.setLoadOnStartup(1);
		appServlet.addMapping("/");
		
	}
}