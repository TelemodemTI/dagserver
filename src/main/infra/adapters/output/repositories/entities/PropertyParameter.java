package main.infra.adapters.output.repositories.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import main.application.pojos.PropertyParameterPOJO;

@Entity
@Table(name="sche_properties")
public class PropertyParameter extends PropertyParameterPOJO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="prop_id",nullable=false)
	private Integer id;
	
	@Column(name = "prop_name", nullable = false)
	private String name;
	
	@Column(name = "prop_group", nullable = false)
	private String group;
	
	@Column(name = "prop_value", nullable = false)
	private String value;
	

	@Column(name = "prop_description", nullable = false)
	private String description;
	
	
}
