package main.cl.dagserver.infra.adapters.input.calcite;

import java.sql.SQLException;

import javax.annotation.PostConstruct;

import org.apache.calcite.avatica.jdbc.JdbcMeta;
import org.apache.calcite.avatica.remote.LocalService;
import org.apache.calcite.avatica.remote.Service;
import org.apache.calcite.avatica.server.AvaticaJsonHandler;
import org.apache.calcite.avatica.server.HttpServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

@Component
@ImportResource("classpath:properties-config.xml")
public class CalciteAvaticaServer {

	@Value("${param.calcite.port}")
	private Integer port;
	
	
	@SuppressWarnings("rawtypes")
	@PostConstruct
	public void startJdbc() throws SQLException, InterruptedException {
		JdbcMeta meta = new JdbcMeta("jdbc:calcite:model=./src/main/resources/model.json");
        Service service = new LocalService(meta);
        AvaticaJsonHandler jsonHandler = new AvaticaJsonHandler(service);
        HttpServer server = new HttpServer.Builder()
                .withHandler(jsonHandler)
                .withPort(port)
                .build();
        server.start();
        server.join();
	}
}
