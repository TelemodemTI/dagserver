package main.cl.dagserver.infra.adapters.output.repositories.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="sche_properties")
public class PropertyParameter {

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
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
