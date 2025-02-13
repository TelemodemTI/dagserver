package main.cl.dagserver.infra.adapters.operators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.row.RowProxy;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;

import main.cl.dagserver.application.ports.input.InternalOperatorUseCase;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.DataFrameUtils;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.CredentialsDTO;
import main.cl.dagserver.infra.adapters.confs.ApplicationContextUtils;


@Operator(args={"host","credentials","port","mode"},optionalv = {"xcom","exchange","routingKey","queue","body"})
public class RabbitMQOperator extends OperatorStage {

	@Override
	public DataFrame call() throws DomainException {		
		log.debug(this.getClass()+" init "+this.name);
		log.debug("args");
		List<Map<String,Object>> rv = new ArrayList<>();
		try {
			Channel channel = this.getConnection();
			log.debug(this.args);
			String mode = this.args.getProperty("mode");
			if(mode.equals("publish")) {
				if(this.optionals.containsKey("xcom")) {
					String xcomname = this.optionals.getProperty("xcom");
					DataFrame df = (DataFrame) this.xcom.get(xcomname);
					Integer count = 0;
					for (Iterator<RowProxy> iterator = df.iterator(); iterator.hasNext();) {
						RowProxy map = iterator.next();
						JSONObject item = new JSONObject(map);
						channel.basicPublish(this.optionals.getProperty("exchange"), this.optionals.getProperty("routingKey"), null, item.toString().getBytes());
						Map<String,Object> dm = new HashMap<String,Object>();
						dm.put("status", "message "+ count.toString() + " published");
						rv.add(dm);
						count++;
					}	
				} else {
					var msg = this.optionals.getProperty("body");
					channel.basicPublish(this.optionals.getProperty("exchange"), this.optionals.getProperty("routingKey"), null, msg.getBytes());
				}
			} else if(mode.equals("consume")) {
				
				while(true) {
					boolean autoAck = false;
					GetResponse response = channel.basicGet(this.optionals.getProperty("queue"), autoAck);
					if (response != null) {
					    byte[] body = response.getBody();
					    long deliveryTag = response.getEnvelope().getDeliveryTag();
					    String msg = new String(body);
					    channel.basicAck(deliveryTag, false);
					    Map<String, Object> dm = new HashMap<String, Object>();
					    dm.put("message", msg);
					    rv.add(dm);
					} else {
						break;
					}
				}
				
			}
			log.debug(this.getClass()+" end "+this.name);
			return DataFrameUtils.buildDataFrameFromMap(rv);
		} catch (Exception e) {
			log.error(e);			
			throw new DomainException(e);
		}
	}
	

	@Override
	public JSONObject getMetadataOperator() {
		MetadataManager metadata = new MetadataManager("main.cl.dagserver.infra.adapters.operators.RabbitMQOperator");
		metadata.setType("MQ");
		metadata.setParameter("host", "text");
		metadata.setParameter("credentials", "credentials");
		metadata.setParameter("port", "number");
		metadata.setParameter("mode", "list", Arrays.asList("publish","consume"));
		metadata.setOpts("xcom", "xcom");
		metadata.setOpts("exchange", "text");
		metadata.setOpts("routingKey", "text");
		metadata.setOpts("queue", "text");
		metadata.setOpts("body", "sourcecode");
		return metadata.generate();
	}
	@Override
	public String getIconImage() {
		return "rabbit.png";
	}
	@SuppressWarnings("static-access")
	private Channel getConnection() throws IOException, TimeoutException, DomainException {
		
		ApplicationContext appCtx = new ApplicationContextUtils().getApplicationContext();
		CredentialsDTO credentials = null;
		if(appCtx != null) {
			var handler =  appCtx.getBean("internalOperatorService", InternalOperatorUseCase.class);
			credentials = handler.getCredentials(this.args.getProperty("credentials"));	
		}
		if(credentials == null) {
			throw new DomainException(new Exception("invalid credentials entry in keystore"));
		}
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername(credentials.getUsername());
		factory.setPassword(credentials.getPassword());
		factory.setHost(this.args.getProperty("host"));
		factory.setPort(Integer.parseInt(this.args.getProperty("port")));

		Connection conn = factory.newConnection();	
		Channel channel = conn.createChannel();
		return channel;
	}	
	
}
