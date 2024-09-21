package main.cl.dagserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class})
@SpringBootApplication()
@ComponentScan(basePackages = "main.cl.dagserver")
public class DagserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(DagserverApplication.class, args);
	}

}
