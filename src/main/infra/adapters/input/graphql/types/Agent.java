package main.infra.adapters.input.graphql.types;

import java.io.Serializable;

public class Agent implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;
	private String hostname;
	private Long updatedOn;
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
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public Long getUpdatedOn() {
		return updatedOn;
	}
	public void setUpdatedOn(Long updatedOn) {
		this.updatedOn = updatedOn;
	}
}