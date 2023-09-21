package main.infra.adapters.input.rabbit;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import main.application.ports.input.RabbitChannelUseCase;

@Component
@ImportResource("classpath:properties-config.xml")
public class RabbitChannel {

	@Value( "${param.rabbit.refresh.timeout}" )
	private Integer rabbitRefresh;
	
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(RabbitChannel.class);
	
	@Autowired
	RabbitChannelUseCase handler;
	
	private Thread listener;
	
	@PostConstruct
	public void listener() {
		listener = new Thread(new Runnable() {
            public void run() {
            	while(true) {
            		try {
            			Properties rabbitprops = handler.getRabbitChannelProperties();
            			String status = rabbitprops.getProperty("STATUS");
            			if(status != null && status.equals("ACTIVE")){
            				configureListener(rabbitprops);
            			}
            			Thread.sleep(rabbitRefresh);	
					} catch (Exception e) {
						log.error(e);
					}
        		}
            }
        });
		listener.start(); 
	}
	
	
	private void configureListener(Properties rabbitprops) {
		try {
				ConnectionFactory factory = new ConnectionFactory();
				factory.setUsername(rabbitprops.getProperty("username"));
				factory.setPassword(rabbitprops.getProperty("password"));
				factory.setHost(rabbitprops.getProperty("host"));
				factory.setPort(Integer.parseInt(rabbitprops.getProperty("port")));

				Connection conn = factory.newConnection();	
				Channel channel = conn.createChannel();
				boolean autoAck = false;
				List<String> queues = Arrays.asList(rabbitprops.getProperty("queues").split("\\,"));
				for (Iterator<String> iterator = queues.iterator(); iterator.hasNext();) {
					String string = iterator.next();
					channel.basicConsume(string, autoAck, rabbitprops.getProperty("myConsumerTag"), new DefaultConsumer(channel) {
				         @Override
				         public void handleDelivery(String consumerTag,
				                                    Envelope envelope,
				                                    AMQP.BasicProperties properties,
				                                    byte[] body) throws IOException {
				             String routingKey = envelope.getRoutingKey();
				             String contentType = properties.getContentType();
				             long deliveryTag = envelope.getDeliveryTag();
				             String bodyStr = new String(body,"UTF-8");
				             
				             handler.raiseEvent(bodyStr,string,routingKey,contentType);
				             channel.basicAck(deliveryTag, false);
				         }
					});
				}		
			} catch (Exception e) {
				log.error(e);
			}
	}
	
}
