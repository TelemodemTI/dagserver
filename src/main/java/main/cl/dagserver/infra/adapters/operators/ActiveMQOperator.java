package main.cl.dagserver.infra.adapters.operators;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.json.JSONObject;

import joinery.DataFrame;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;
import javax.jms.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Operator(args = {"mode", "brokerURL" , "queueName"}, optionalv = {"xcom","timeout"})
public class ActiveMQOperator extends OperatorStage {

    @SuppressWarnings("rawtypes")
	@Override
    public DataFrame call() throws DomainException {
        String mode = this.args.getProperty("mode");
        if ("produce".equalsIgnoreCase(mode)) {
            produce();
            return this.createStatusFrame("ok");
        } else if ("consume".equalsIgnoreCase(mode)) {
        	return consume();
        } else {
            throw new IllegalArgumentException("Unsupported mode: " + mode);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void produce() throws DomainException {
        try {
            String brokerURL = this.args.getProperty("brokerURL");
            String queueName = this.args.getProperty("queueName");
            String xcomname = this.optionals.getProperty("xcom");
            if(xcomname != null && !xcomname.isEmpty()) {
				if(!this.xcom.has(xcomname)) {
					throw new DomainException(new Exception("xcom not exist for dagname::"+xcomname));
				}
            }
            
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);
            Connection connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(queueName);
            MessageProducer producer = session.createProducer(destination);

            DataFrame data = (DataFrame) this.xcom.get(xcomname);
            for (Iterator<Map<String, Object>> iterator = data.iterrows(); iterator.hasNext();) {
    				Object map = iterator.next();
    				TextMessage message = new ActiveMQTextMessage();
    				String messageStr = map.toString();
                    message.setText(messageStr);
                    producer.send(message);
            }
            log.debug("Message sent to queue: " + queueName);
            connection.close();
	    } catch (Exception e) {
	        throw new DomainException(e);
	    } 
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	private DataFrame consume() throws DomainException {
        try {
            String brokerURL = this.args.getProperty("brokerURL");
            String queueName = this.args.getProperty("queueName");
            String timeoutStr = this.optionals.getProperty("timeout");
            Integer timeout = 10000;
            if(timeoutStr != null && !timeoutStr.isEmpty()) {
            	timeout = Integer.parseInt(timeoutStr);
            }
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);
            Connection connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(queueName);
            MessageConsumer consumer = session.createConsumer(destination);
            DataFrame df = new DataFrame();
            List<Map<String,Object>> rv = new ArrayList<>();
            while (true) {
                    Message message = consumer.receive(timeout);
                    if (message instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) message;
                        Map<String,Object> map = new HashMap<String,Object>();
                        map.put("messageId", textMessage.getJMSMessageID());
                        map.put("text", textMessage.getText());
                        map.put("jmsExpiration", textMessage.getJMSExpiration());
                        map.put("jmsCorrelationID", textMessage.getJMSCorrelationID());
                        map.put("jmsPriority", textMessage.getJMSPriority());
                        map.put("jmsReplyTo", textMessage.getJMSReplyTo());
                    	rv.add(map);
                    } else if (message == null) {
                        break; // Salir si no hay más mensajes
                    }
            }
            df.add(rv);
            return df;
        } catch (Exception e) {
            throw new DomainException(e);
        }
    }

    @Override
    public String getIconImage() {
        return "activemq.png";
    }

    @Override
    public JSONObject getMetadataOperator() {
        MetadataManager metadata = new MetadataManager("main.cl.dagserver.infra.adapters.operators.ActiveMQOperator");
        metadata.setParameter("mode", "list", List.of("produce", "consume"));
        metadata.setParameter("queueName", "text");
        metadata.setParameter("brokerURL","text");
        metadata.setOpts("xcom","xcom");
        metadata.setOpts("timeout","number");
        return metadata.generate();
    }
}
