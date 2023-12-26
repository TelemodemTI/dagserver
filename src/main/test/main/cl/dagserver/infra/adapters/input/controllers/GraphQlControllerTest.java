package main.cl.dagserver.infra.adapters.input.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import main.cl.dagserver.infra.adapters.confs.RequestQuery;
import main.cl.dagserver.infra.adapters.input.graphql.MutationResolver;
import main.cl.dagserver.infra.adapters.input.graphql.QueryResolver;


class GraphQlControllerTest {

	
	private GraphQlController controller; 
	
	@Mock
	QueryResolver queryResolver;

	@Mock
	MutationResolver mutationResolver;
	
	@Mock
	GraphQL graphQL;
	
	@BeforeEach
    public void init() {
		queryResolver = mock(QueryResolver.class);
		mutationResolver = mock(MutationResolver.class);
		graphQL = mock(GraphQL.class);
		controller = new GraphQlController(queryResolver,mutationResolver);
		ReflectionTestUtils.setField(controller, "queryResolver", queryResolver);
		ReflectionTestUtils.setField(controller, "graphQL", graphQL);
		ReflectionTestUtils.setField(controller, "mutationResolver", mutationResolver);
		
    }
	
    
    @Test
    void myGraphqlTest() {
    	Map<String, Object> resturnedmap = new HashMap<>();
    	resturnedmap.put("test", "test");
    	RequestQuery req = mock(RequestQuery.class);
    	ExecutionResult executionResult = mock(ExecutionResult.class);
    	when(graphQL.execute(any(ExecutionInput.class))).thenReturn(executionResult);
    	when(executionResult.toSpecification()).thenReturn(resturnedmap);
    	var map = controller.myGraphql(req);
    	assertEquals(resturnedmap.get("test"),map.get("test"));
    }
    @Test
    void myGraphqlTest2() {
    	Map<String, Object> resturnedmap = new HashMap<>();
    	resturnedmap.put("test", "test");
    	ExecutionResult executionResult = mock(ExecutionResult.class);
    	when(graphQL.execute(any(ExecutionInput.class))).thenReturn(executionResult);
    	when(executionResult.toSpecification()).thenReturn(resturnedmap);
    	var map = controller.myGraphql("test");
    	assertEquals(resturnedmap.get("test"),map.get("test"));
    }
}
