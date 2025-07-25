package main.cl.dagserver.application.pojos;

import lombok.Data;

@Data
public class AgentPOJO {

	private Integer id;
	private String name;
	private String hostname;
	private Long updatedOn;
	
}
