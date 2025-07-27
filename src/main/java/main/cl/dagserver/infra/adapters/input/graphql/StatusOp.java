package main.cl.dagserver.infra.adapters.input.graphql;

import lombok.Data;

@Data
public class StatusOp {
	private String status;
    private Integer code;
    private String value;
}