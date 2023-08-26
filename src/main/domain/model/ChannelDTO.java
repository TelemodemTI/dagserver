package main.domain.model;

import java.util.Map;

public class ChannelDTO {

	private String name;
	private String status;
	private Map<String,String> props;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Map<String, String> getProps() {
		return props;
	}
	public void setProps(Map<String, String> props) {
		this.props = props;
	}
}
