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
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import joinery.DataFrame;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;


@Operator(args={"host","username","password","port","mode"},optionalv = {"xcom","exchange","routingKey","queue","body"})
public class RabbitMQOperator extends OperatorStage {

	@SuppressWarnings({ "unchecked", "rawtypes" })
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
					for (Iterator<Map<String, Object>> iterator = df.iterrows(); iterator.hasNext();) {
						Map<String, Object> map = iterator.next();
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
			return this.buildDataFrame(rv);
		} catch (Exception e) {
			log.error(e);			
			throw new DomainException(e);
		}
	}
	

	@Override
	public JSONObject getMetadataOperator() {
		MetadataManager metadata = new MetadataManager("main.cl.dagserver.infra.adapters.operators.RabbitMQOperator");
		metadata.setParameter("host", "text");
		metadata.setParameter("username", "text");
		metadata.setParameter("password", "password");
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
	private Channel getConnection() throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername(this.args.getProperty("username"));
		factory.setPassword(this.args.getProperty("password"));
		factory.setHost(this.args.getProperty("host"));
		factory.setPort(Integer.parseInt(this.args.getProperty("port")));

		Connection conn = factory.newConnection();	
		Channel channel = conn.createChannel();
		return channel;
	}	
}
