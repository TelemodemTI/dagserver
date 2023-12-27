package main.cl.dagserver.infra.adapters.input.rabbit;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
	
	private static final String QUEUE = "queue";
	private Boolean someCondition = false;
	private RabbitChannelUseCase handler;
    private ApplicationEventPublisher eventPublisher;
	private List<Map<String,String>> runningConsumers;
	private Channel channel1;
	
	@Autowired
	public RabbitChannel(RabbitChannelUseCase handler,ApplicationEventPublisher eventPublisher){
		this.handler = handler;
		this.eventPublisher = eventPublisher;
	}
	
	@PostConstruct
	public void listenerHandler() {
		runningConsumers = new ArrayList<>();
		Thread listener = new Thread(()-> {
            	try {
					runForever();
            	} catch (InterruptedException ie) {
            		Thread.currentThread().interrupt();
            	} catch (Exception e) {
					eventPublisher.publishEvent(new ExceptionEventLog(this, new DomainException(e), "listenerHandler"));
				}
        });
		listener.start(); 
	}	
	private void runForever() throws IOException, TimeoutException, InterruptedException, DomainException {
		Boolean longRunning = true;
    	while(longRunning.equals(Boolean.TRUE)) {
       		Properties rabbitprops = handler.getRabbitChannelProperties();
    		String status = rabbitprops.getProperty("STATUS");
    		if(status != null && status.equals("ACTIVE")){
    			if(channel1 == null) {
    				channel1 = getConnection(rabbitprops);	
    			}
    			if (someCondition.equals(Boolean.TRUE)) {
    				longRunning = false;
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
			return conn.createChannel();
		}
	}	
	private void configureListener(Properties rabbitprops) throws IOException {
		Set<Object> keys = rabbitprops.keySet();
		List<String> queues = new ArrayList<>();
		for (Object key : keys) {
            String clave = (String) key;
            String valor = rabbitprops.getProperty(clave);
            if(valor.equals("rabbit_consumer_queue")) {
            	queues.add(clave);
            }
        }
		if(validateDeleteConsumer(queues).equals(Boolean.TRUE)) {
			removeConsumers(queues);
		}
		if(validateNewConsumer(queues).equals(Boolean.TRUE)) {
			addConsumers(queues);
		}
		
	}
	private void removeConsumers(List<String> queues) throws IOException {
		for (Iterator<Map<String,String>> iterator = runningConsumers.iterator(); iterator.hasNext();) {
			Map<String,String> runningConsumer = iterator.next();
			String queueRunning = runningConsumer.get(QUEUE);
			String consumerTagRunning = runningConsumer.get("consumerTag");
			if(!queues.contains(queueRunning)) {
				channel1.basicCancel(consumerTagRunning);
			}
		}
	}
	private Boolean validateDeleteConsumer(List<String> queues) {
		Boolean rv = false;
		for (Iterator<Map<String,String>> iterator = runningConsumers.iterator(); iterator.hasNext();) {
			Map<String,String> runningConsumer = iterator.next();
			String queueRunning = runningConsumer.get(QUEUE);
			if(!queues.contains(queueRunning)) {
				rv = true;
				break;
			}
		}
		return rv;
	}
	private void addConsumers(List<String> queues) throws IOException {
		boolean autoAck = false;
		for (Iterator<String> iterator = queues.iterator(); iterator.hasNext();) {
			String string = iterator.next();
			String consumerTagIn = string + "_consumerTag";
			String consumerTag = channel1.basicConsume(string, autoAck, consumerTagIn, new DefaultConsumer(channel1) {
		         @Override
		         public void handleDelivery(String consumerTag,
		                                    Envelope envelope,
		                                    AMQP.BasicProperties properties,
		                                    byte[] body) throws IOException {
		             String routingKey = envelope.getRoutingKey();
		             String contentType = properties.getContentType();
		             long deliveryTag = envelope.getDeliveryTag();
		             String bodyStr = new String(body,StandardCharsets.UTF_8);    
		             try {
		            	handler.raiseEvent(bodyStr,string,routingKey,contentType);	
		             } catch (Exception e) {
		            	 eventPublisher.publishEvent(new ExceptionEventLog(this, new DomainException(e), "addConsumers"));
		             }
		             
		             channel1.basicAck(deliveryTag, false);
		         }
			});
			Map<String,String> item = new HashMap<>();
			item.put(QUEUE, string);
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
				 String queueRunning = runningConsumer.get(QUEUE);
				 if(queueRunning.equals(queue)) {
					 exist = true;
					 break;
				 }
			}
			if(exist.equals(Boolean.FALSE)) {
				rv = true;
				break;
			}
		}
		return rv;
	}

	public void setSomeCondition(Boolean someCondition) {
		this.someCondition = someCondition;
	}

	public void setChannel1(Channel channel1) {
		this.channel1 = channel1;
	}

	public void setRunningConsumers(List<Map<String, String>> runningConsumers) {
		this.runningConsumers = runningConsumers;
	}
	
}
