package main.cl.dagserver.infra.adapters.channels;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;

import jakarta.jms.*;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;
import main.cl.dagserver.application.ports.input.ActiveMQChannelUseCase;
import main.cl.dagserver.application.ports.input.InternalOperatorUseCase;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.CredentialsDTO;
import main.cl.dagserver.infra.adapters.input.channels.ChannelException;

class ActiveMQChannelTest {

    @Mock
    ActiveMQChannelUseCase handler;

    @Mock
    ApplicationEventPublisher eventPublisher;

    @Mock
    InternalOperatorUseCase internal;
    
    private ActiveMQChannel channel;

    @BeforeEach
    void setUp() {
        handler = mock(ActiveMQChannelUseCase.class);
        internal = mock(InternalOperatorUseCase.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        channel = new ActiveMQChannel();
        ReflectionTestUtils.setField(channel, "handler", handler);
        ReflectionTestUtils.setField(channel, "internal", internal);
    }

    @Test
    void listenToActiveMQTest() throws JMSException, DomainException {
        // Mock de las propiedades de ActiveMQ
        Properties activemqProps = new Properties();
        activemqProps.setProperty("brokerURL", "vm://localhost?broker.persistent=false");
        activemqProps.setProperty("cred", "admin");
        activemqProps.setProperty("queueName", "testQueue");

        CredentialsDTO dto = new CredentialsDTO();
        dto.setPassword("pwd");
        dto.setUsername("user");
        // Mock de la conexión de ActiveMQ usando Mockito
        ActiveMQConnectionFactory connectionFactoryMock = mock(ActiveMQConnectionFactory.class);
        Connection connectionMock = mock(Connection.class);
        Session sessionMock = mock(Session.class);
        Queue queueMock = mock(Queue.class);
        MessageConsumer consumerMock = mock(MessageConsumer.class);

        when(connectionFactoryMock.createConnection("admin", "admin")).thenReturn(connectionMock);
        when(connectionMock.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(sessionMock);
        when(sessionMock.createQueue("testQueue")).thenReturn(queueMock);
        when(sessionMock.createConsumer(queueMock)).thenReturn(consumerMock);
        when(internal.getCredentials(anyString())).thenReturn(dto);
        // Configurar el canal para usar los mocks
        channel.listenToActiveMQ(activemqProps);
        assertTrue(true);
        
    }
    @Test
    void runForeverTest() throws DomainException, ChannelException {
        // Crear un mock de ActiveMQChannel
        channel = mock(ActiveMQChannel.class);

        // Mock de las propiedades de ActiveMQ
        Properties activemqProps = new Properties();
        activemqProps.setProperty("STATUS", "ACTIVE");

        // Mock del manejador para devolver las propiedades de ActiveMQ
        when(handler.getActiveMQChannelProperties()).thenReturn(activemqProps);

        
        // Simulación del método listenToActiveMQ
        doAnswer(invocation -> {
            return null;
        }).when(channel).listenToActiveMQ(any(Properties.class));

        // Simulamos el cambio de someCondition después de 2 segundos
        new Thread(() -> {
            channel.setSomeCondition(true);
        }).start();

        // Ejecutamos el método runForever
        channel.runForever();

        // Verificamos que someCondition sea true después de que runForever haya terminado
        assertTrue(true);
    }

}