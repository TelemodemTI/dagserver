package main.cl.dagserver.infra.adapters.input.channels.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import main.cl.dagserver.application.ports.input.ActiveMQChannelUseCase;
import main.cl.dagserver.domain.core.ExceptionEventLog;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.infra.adapters.input.channels.InputChannel;

import javax.jms.*;
import java.util.Properties;

@Component
public class ActiveMQChannel extends InputChannel {

    @Value("${param.activemq.refresh.timeout}")
    private Integer activemqRefresh;
    private ActiveMQChannelUseCase handler;
    private static final String LISTENER = "activemqListener";

    @Autowired
    public ActiveMQChannel(ActiveMQChannelUseCase handler, ApplicationEventPublisher eventPublisher) {
        super(eventPublisher);
        this.handler = handler;
    }

    public void runForever() throws DomainException, InterruptedException {
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
    }

    private void listenToActiveMQ(Properties activemqProps) throws DomainException {
        String brokerURL = activemqProps.getProperty("brokerURL");
        String username = activemqProps.getProperty("username");
        String password = activemqProps.getProperty("password");
        String queueName = activemqProps.getProperty("queueName");

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection(username, password);
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(queueName);
            MessageConsumer consumer = session.createConsumer(queue);

            // Implementa el MessageListener para manejar los mensajes recibidos
            consumer.setMessageListener(message -> {
                if (message instanceof TextMessage) {
                    try {
                    	handler.raiseEvent(queueName,((TextMessage) message).getText() );
                    } catch (JMSException | DomainException e) {
                    	eventPublisher.publishEvent(new ExceptionEventLog(this, new DomainException(e), LISTENER));
                    }
                }
            });

            // Mantén el programa en ejecución para recibir mensajes
            Thread.sleep(10000); // Por ejemplo, espera 10 segundos

            connection.close(); // Cierra la conexión cuando hayas terminado
        } catch (JMSException | InterruptedException e) {
            throw new DomainException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}