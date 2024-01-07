package main.cl.dagserver.infra.adapters.input.graphql.types;

import java.util.List;

public class Channel {
	private String name;
	private String status;
	private String icon;
	private List<ChannelProps> props;
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
	public List<ChannelProps> getProps() {
		return props;
	}
	public void setProps(List<ChannelProps> props) {
		this.props = props;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
}
