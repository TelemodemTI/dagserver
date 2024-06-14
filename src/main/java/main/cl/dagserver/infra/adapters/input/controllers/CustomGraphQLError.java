package main.cl.dagserver.infra.adapters.input.controllers;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.List;
import java.util.Map;

public class CustomGraphQLError implements GraphQLError {


	private static final long serialVersionUID = 1L;
	private final String message;

    public CustomGraphQLError(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public List<SourceLocation> getLocations() {
        return null;
    }

    @Override
    public Map<String, Object> getExtensions() {
        return null;
    }

    @Override
    public List<Object> getPath() {
        return null;
    }

    @Override
    public Map<String, Object> toSpecification() {
        return Map.of("message", getMessage());
    }

    @Override
    public ErrorType getErrorType() {
        return ErrorType.DataFetchingException;
    }
}
