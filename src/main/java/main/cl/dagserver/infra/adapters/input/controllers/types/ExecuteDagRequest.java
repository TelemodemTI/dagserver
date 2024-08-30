package main.cl.dagserver.infra.adapters.input.controllers.types;

import java.util.Map;

import lombok.Data;

@Data
public class ExecuteDagRequest {
	private String jarname;
	private String dagname;
	private Map<String,String> args;
}
