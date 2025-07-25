package main.cl.dagserver.application.pojos;

import lombok.Data;

@Data
public class ExceptionsPOJO {

	private String eventDt;
    private String classname;
    private String method;
    private String stack;
    private String evalkey;
	   	
}
