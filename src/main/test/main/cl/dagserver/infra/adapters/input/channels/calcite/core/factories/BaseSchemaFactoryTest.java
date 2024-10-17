package main.cl.dagserver.infra.adapters.input.channels.calcite.core.factories;

import static org.junit.jupiter.api.Assertions.*;


import java.util.Map;

import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaPlus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import main.cl.dagserver.infra.adapters.input.channels.calcite.core.schemas.BaseApiSchema;

class BaseSchemaFactoryTest {

    private BaseSchemaFactory baseSchemaFactory;

    @Mock
    private SchemaPlus mockParentSchema;

    @Mock
    private Map<String, Object> mockOperand;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        baseSchemaFactory = new BaseSchemaFactory();
    }

    @Test
    void testCreate_shouldReturnBaseApiSchema() {
        // Arrange
        String schemaName = "testSchema";

        // Act
        Schema result = baseSchemaFactory.create(mockParentSchema, schemaName, mockOperand);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof BaseApiSchema, "Expected a BaseApiSchema instance");
    }
}
