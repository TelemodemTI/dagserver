package main.cl.dagserver.infra.adapters.input.graphql.types;

import java.util.List;

public class Detail {
	private String dagname;
	private String cronExpr;
	private String group;
	private String onStart;
	private String onEnd;
	private List<Node> node;
	public String getDagname() {
		return dagname;
	}
	public void setDagname(String dagname) {
		this.dagname = dagname;
	}
	public List<Node> getNode() {
		return node;
	}
	public void setNode(List<Node> node) {
		this.node = node;
	}
	public String getCronExpr() {
		return cronExpr;
	}
	public void setCronExpr(String cronExpr) {
		this.cronExpr = cronExpr;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getOnStart() {
		return onStart;
	}
	public void setOnStart(String onStart) {
		this.onStart = onStart;
	}
	public String getOnEnd() {
		return onEnd;
	}
	public void setOnEnd(String onEnd) {
		this.onEnd = onEnd;
	}
	
}
