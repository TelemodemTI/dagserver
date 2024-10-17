package main.cl.dagserver.infra.adapters.input.channels.calcite.core.schemas;

import static org.junit.jupiter.api.Assertions.*;
import org.apache.calcite.schema.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.google.common.collect.Multimap;
import main.cl.dagserver.application.ports.input.CalciteUseCase;
import main.cl.dagserver.domain.services.SchedulerQueryHandlerService;

class BaseApiSchemaTest {

    @InjectMocks
    private BaseApiSchema baseApiSchema;

    @Mock
    private SchedulerQueryHandlerService schedulerQueryHandlerService;

    @Mock
    private CalciteUseCase calciteUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetFunctionMultimap_shouldReturnCorrectFunctions() {
        // Act
        Multimap<String, Function> functionMultimap = baseApiSchema.getFunctionMultimap();

        // Assert
        assertNotNull(functionMultimap, "Function multimap should not be null");
        assertEquals(1, functionMultimap.size(), "Function multimap should contain 1 entry");

        assertTrue(functionMultimap.containsKey("DAG_EXECUTOR"), "Function multimap should contain key 'DAG_EXECUTOR'");
        assertFalse(functionMultimap.get("DAG_EXECUTOR").isEmpty(), "'DAG_EXECUTOR' should not be empty");
    }

}
