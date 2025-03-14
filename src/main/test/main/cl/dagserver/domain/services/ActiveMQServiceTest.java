package main.cl.dagserver.domain.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import main.cl.dagserver.application.ports.output.SchedulerRepositoryOutputPort;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.PropertyParameterDTO;

class ActiveMQServiceTest {
    
    private ActiveMQService service = new ActiveMQService();
    
    @Mock
    private SchedulerRepositoryOutputPort repository;
    
    @BeforeEach
    void init() {
        repository = mock(SchedulerRepositoryOutputPort.class);
        ReflectionTestUtils.setField(service, "repository", repository);
        ReflectionTestUtils.setField(service, "activemqPropkey", "activemqPropkey");
    }
    
    @Test
    void getActiveMQChannelPropertiesTest() throws DomainException {
        PropertyParameterDTO expectedProps = new PropertyParameterDTO();
        expectedProps.setId(1);
        expectedProps.setName("test");
        expectedProps.setValue("activemq_consumer_listener");
        PropertyParameterDTO expectedProps1 = new PropertyParameterDTO();
        expectedProps1.setId(1);
        expectedProps1.setName("test");
        expectedProps1.setValue("none");
        List<PropertyParameterDTO> list = new ArrayList<>();
        list.add(expectedProps);
        list.add(expectedProps1);
        when(repository.getProperties(anyString())).thenReturn(list);
        Properties actualProps = service.getActiveMQChannelProperties();
        assertNotNull(actualProps);
    }
    
    @Test
    void raiseEventTest() throws DomainException {
        String expectedChannel = "testChannel";
        String expectedMessage = "testMessage";
        service.raiseEvent(expectedChannel, expectedMessage);
        assertTrue(true);
    }
    
    @Test
    void getActiveMQListenersTest() throws DomainException {
        List<PropertyParameterDTO> propertyList = new ArrayList<>();
        PropertyParameterDTO dto1 = new PropertyParameterDTO();
        dto1.setName("prop1");
        dto1.setValue("activemq_consumer_listener");
        PropertyParameterDTO dto2 = new PropertyParameterDTO();
        dto2.setName("prop2");
        dto2.setValue("other_value");
        propertyList.add(dto1);
        propertyList.add(dto2);
        when(repository.getProperties(anyString())).thenReturn(propertyList);
        Properties actualProps = service.getActiveMQListeners();
        assertEquals(1, actualProps.size());
        assertEquals("activemq_consumer_listener", actualProps.getProperty("prop1"));
    }
    
}