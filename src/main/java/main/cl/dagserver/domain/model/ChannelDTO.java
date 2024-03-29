package main.cl.dagserver.domain.model;

import java.util.List;

public class ChannelDTO {

	private String name;
	private String status;
	private String icon;
	private List<ChannelPropsDTO> props;
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
	public List<ChannelPropsDTO> getProps() {
		return props;
	}
	public void setProps(List<ChannelPropsDTO> props) {
		this.props = props;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
}
