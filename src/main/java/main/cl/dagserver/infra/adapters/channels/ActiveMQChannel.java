package main.cl.dagserver.infra.adapters.channels;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.extern.log4j.Log4j2;
import main.cl.dagserver.application.ports.input.ActiveMQChannelUseCase;
import main.cl.dagserver.domain.core.ExceptionEventLog;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.infra.adapters.input.channels.ChannelException;
import main.cl.dagserver.infra.adapters.input.channels.InputChannel;
import jakarta.jms.*;
import java.util.Arrays;
import java.util.Properties;

@Component
@Log4j2
public class ActiveMQChannel extends InputChannel {

    @Value("${param.activemq.refresh.timeout}")
    private Integer activemqRefresh;
    private static final String LISTENER = "activemqListener";

    @Autowired
    private ActiveMQChannelUseCase handler;
  
    public void runForever() throws ChannelException {
        try {
        	boolean keepRunning = true;
            while (keepRunning) {
                Properties activemqProps = handler.getActiveMQChannelProperties();
                String status = activemqProps.getProperty("STATUS");
                if (status != null && status.equals("ACTIVE")) {
                    listenToActiveMQ(activemqProps);
                }
                if (someCondition.equals(Boolean.TRUE)) {
                    keepRunning = false;
                }
                Thread.sleep(activemqRefresh);
            }	
        } catch (InterruptedException ie) {
            log.error("InterruptedException: ", ie);
            Thread.currentThread().interrupt(); // Vuelve a establecer la interrupción
            throw new ChannelException(ie); // Relanza la excepción para que sea manejada en el método principal
        } catch (DomainException e) {
			throw new ChannelException(e);
		}
    	
    }

    public void peekMessages(String brokerURL,String username,String password,String queueName) throws DomainException {
    	ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);
        connectionFactory.setTrustedPackages(Arrays.asList("main.cl.dagserver"));
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection(username, password);
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(queueName);
            MessageConsumer consumer = session.createConsumer(queue);

            // Implementa el MessageListener para manejar los mensajes recibidos
            consumer.setMessageListener(message -> {
                if (message instanceof TextMessage textmessage) {
                    log.info(textmessage);
                	try {
                        handler.raiseEvent(queueName,((TextMessage) message).getText() );
                    } catch (JMSException | DomainException e) {
                        eventPublisher.publishEvent(new ExceptionEventLog(this, new DomainException(e), LISTENER));
                    }
                }
            });

            // Mantén el programa en ejecución para recibir mensajes
            Thread.sleep(10000); // Por ejemplo, espera 10 segundos

        } catch (InterruptedException ie) {
            log.error("InterruptedException: ", ie);
            Thread.currentThread().interrupt(); // Vuelve a establecer la interrupción
            throw new DomainException(ie); // Relanza la excepción para que sea manejada en el método principal
        } catch (JMSException e) {
            throw new DomainException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    log.error(e);
                    // Puedes manejar esta excepción aquí o relanzarla si es necesario
                }
            }
        }	

    }
    
    
    public void listenToActiveMQ(Properties activemqProps) throws DomainException {
        String brokerURL = activemqProps.getProperty("host");
        String username = activemqProps.getProperty("user");
        String password = activemqProps.getProperty("pwd");
        activemqProps.stringPropertyNames().stream()
            .filter(key -> key.startsWith("queue_"))
            .forEach(queueKey -> {
                String queueName = activemqProps.getProperty(queueKey);
                if (queueName != null && !queueName.isEmpty()) {
                    try {
                        this.peekMessages(brokerURL, username, password, queueName);
                    } catch (DomainException e) {
                        log.error("Error peeking messages from queue: " + queueName, e);
                    }
                } else {
                    log.info("listenToActiveMQ::no queue name for key: " + queueKey);
                }
            });
    }

}
