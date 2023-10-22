package main.cl.dagserver.infra.adapters.input.rabbit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import main.cl.dagserver.application.ports.input.RabbitChannelUseCase;
import main.cl.dagserver.domain.core.ExceptionEventLog;
import main.cl.dagserver.domain.exceptions.DomainException;


@Component
@ImportResource("classpath:properties-config.xml")
public class RabbitChannel {

	@Value( "${param.rabbit.refresh.timeout}" )
	private Integer rabbitRefresh;
	
	@Autowired
	RabbitChannelUseCase handler;
	
	@Autowired
    private ApplicationEventPublisher eventPublisher;
	
	private Thread listener;
	private List<Map<String,String>> runningConsumers;
	private Channel channel;
	
	@PostConstruct
	public void listenerHandler() {
		runningConsumers = new ArrayList<>();
		listener = new Thread(new Runnable() {
            public void run() {
            	try {
					runForever();
				} catch (Exception e) {
					eventPublisher.publishEvent(new ExceptionEventLog(this, new DomainException(e), "listenerHandler"));
				}
            }
        });
		listener.start(); 
	}	
	private void runForever() throws Exception {
		Boolean longRunning = true;
    	while(longRunning) {
       		Properties rabbitprops = handler.getRabbitChannelProperties();
    		String status = rabbitprops.getProperty("STATUS");
    		if(status != null && status.equals("ACTIVE")){
    			if(channel == null) {
    				channel = getConnection(rabbitprops);	
    			}
    			configureListener(rabbitprops);
    		}
    		Thread.sleep(rabbitRefresh);	
		}
	}
	private Channel getConnection(Properties rabbitprops) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername(rabbitprops.getProperty("username"));
		factory.setPassword(rabbitprops.getProperty("password"));
		factory.setHost(rabbitprops.getProperty("host"));
		factory.setPort(Integer.parseInt(rabbitprops.getProperty("port")));

		try(Connection conn = factory.newConnection();){
			Channel channel = conn.createChannel();
			return channel;	
		}
	}	
	private void configureListener(Properties rabbitprops) throws IOException, TimeoutException {
		Set<Object> keys = rabbitprops.keySet();
		List<String> queues = new ArrayList<>();
		for (Object key : keys) {
            String clave = (String) key;
            String valor = rabbitprops.getProperty(clave);
            if(valor.equals("rabbit_consumer_queue")) {
            	queues.add(clave);
            }
        }
		if(validateNewConsumer(queues)) {
			addConsumers(rabbitprops, queues);
		}
		if(validateDeleteConsumer(queues)) {
			removeConsumers(rabbitprops,queues);
		}
	}
	private void removeConsumers(Properties rabbitprops, List<String> queues) throws IOException {
		for (Iterator<Map<String,String>> iterator = runningConsumers.iterator(); iterator.hasNext();) {
			Map<String,String> runningConsumer = iterator.next();
			String queueRunning = runningConsumer.get("queue");
			String consumerTagRunning = runningConsumer.get("consumerTag");
			if(!queues.contains(queueRunning)) {
				channel.basicCancel(consumerTagRunning);
			}
		}
	}
	private boolean validateDeleteConsumer(List<String> queues) {
		Boolean rv = false;
		for (Iterator<Map<String,String>> iterator = runningConsumers.iterator(); iterator.hasNext();) {
			Map<String,String> runningConsumer = iterator.next();
			String queueRunning = runningConsumer.get("queue");
			if(!queues.contains(queueRunning)) {
				rv = true;
				break;
			}
		}
		return rv;
	}
	private void addConsumers(Properties rabbitprops,List<String> queues) throws IOException {
		boolean autoAck = false;
		for (Iterator<String> iterator = queues.iterator(); iterator.hasNext();) {
			String string = iterator.next();
			String consumerTagIn = string + "_consumerTag";
			String consumerTag = channel.basicConsume(string, autoAck, consumerTagIn, new DefaultConsumer(channel) {
		         @Override
		         public void handleDelivery(String consumerTag,
		                                    Envelope envelope,
		                                    AMQP.BasicProperties properties,
		                                    byte[] body) throws IOException {
		             String routingKey = envelope.getRoutingKey();
		             String contentType = properties.getContentType();
		             long deliveryTag = envelope.getDeliveryTag();
		             String bodyStr = new String(body,"UTF-8");    
		             try {
		            	handler.raiseEvent(bodyStr,string,routingKey,contentType);	
		             } catch (Exception e) {
		            	 eventPublisher.publishEvent(new ExceptionEventLog(this, new DomainException(e), "addConsumers"));
		             }
		             
		             channel.basicAck(deliveryTag, false);
		         }
			});
			Map<String,String> item = new HashMap<>();
			item.put("queue", string);
			item.put("consumerTag", consumerTag);
			runningConsumers.add(item);
		}
	}
	private Boolean validateNewConsumer(List<String> queues) {
		Boolean rv = false;
		for (Iterator<String> iterator = queues.iterator(); iterator.hasNext();) {
			 String queue = iterator.next();
			 Boolean exist = false;
			 for (Iterator<Map<String,String>> iterator2 = runningConsumers.iterator(); iterator2.hasNext();) {
				 Map<String,String> runningConsumer = iterator2.next();
				 String queueRunning = runningConsumer.get("queue");
				 if(queueRunning.equals(queue)) {
					 exist = true;
					 break;
				 }
			}
			if(!exist) {
				rv = true;
				break;
			}
		}
		return rv;
	}
	
}
