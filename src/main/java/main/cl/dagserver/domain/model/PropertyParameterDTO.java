package main.cl.dagserver.domain.model;

import lombok.Data;

@Data
public class PropertyParameterDTO {

	private Integer id;
	private String name;
	private String group;
	private String value;
	private String description;
	
}
