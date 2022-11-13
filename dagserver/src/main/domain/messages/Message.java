package main.domain.messages;

import java.util.List;

public class Message {
	private String type;
	private List<String> args;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<String> getArgs() {
		return args;
	}
	public void setArgs(List<String> args) {
		this.args = args;
	}
}
