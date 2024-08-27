package main.cl.dagserver.infra.adapters.input.controllers;

import java.util.Map;

import lombok.Data;

@Data
public class ExecuteDagRequest {
	private String jarname;
	private String dagname;
	private Map<String,String> args;
}
