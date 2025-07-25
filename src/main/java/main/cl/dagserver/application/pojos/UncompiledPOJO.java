package main.cl.dagserver.application.pojos;

import lombok.Data;

@Data
public class UncompiledPOJO {

	private Integer uncompiledId;
	private String bin;
	private Long createdDt;
	
}
