package main.cl.dagserver.infra.adapters.confs;


import java.util.Map;

import lombok.Data;

@Data
public class RequestQuery {
	private String operationName;
	private String query;
	private Map<String, Object> variables;


}