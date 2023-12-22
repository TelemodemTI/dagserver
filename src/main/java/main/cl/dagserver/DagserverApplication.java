package main.cl.dagserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class})
@SpringBootApplication
public class DagserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(DagserverApplication.class, args);
	}

}
