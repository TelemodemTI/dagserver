package main.cl.dagserver.infra.adapters.input.controllers.types;

import lombok.Data;

@Data
public class DagResultResponse {
	private String status;
	private Object xcom;
}
