package main.cl.dagserver.infra.adapters.input.controllers;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coxautodev.graphql.tools.SchemaParser;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import main.cl.dagserver.infra.adapters.confs.RequestQuery;
import main.cl.dagserver.infra.adapters.input.graphql.MutationResolver;
import main.cl.dagserver.infra.adapters.input.graphql.QueryResolver;


@RestController
@RequestMapping(path = "/query", produces = "application/json")
@CrossOrigin(origins = "*")
public class GraphQlController {
	
	
	@Autowired
	private QueryResolver queryResolver;

	@Autowired
	private MutationResolver mutationResolver;
	
	
	private GraphQL graphQL;
	
	@PostConstruct
	public void init() {		
		GraphQLSchema graphQLSchema = SchemaParser.newParser().file("schema.graphql").resolvers(queryResolver,mutationResolver).build().makeExecutableSchema();
		graphQL = GraphQL.newGraphQL(graphQLSchema).build();
	}
	
	@PostMapping
	public Map<String, Object> myGraphql(@RequestBody RequestQuery query) {
		ExecutionInput executionInput = ExecutionInput.newExecutionInput().query(query.getQuery()).variables(query.getVariables()).operationName(query.getOperationName()).build();
		ExecutionResult executionResult = graphQL.execute(executionInput);
		return executionResult.toSpecification();
	}

	@GetMapping
	public Map<String, Object> myGraphql(@RequestParam String query) {
		ExecutionInput executionInput = ExecutionInput.newExecutionInput().query(query).build();
		ExecutionResult executionResult = graphQL.execute(executionInput);
		return executionResult.toSpecification();
	}
}
