package main.cl.dagserver.domain.model;

import java.util.List;

public class DagDTO {
	private String dagname;
	private List<List<String>> ops;
	private String cronExpr;
	private String group;
	private String onStart;
	private String onEnd;
	public String getDagname() {
		return dagname;
	}
	public void setDagname(String dagname) {
		this.dagname = dagname;
	}
	public List<List<String>> getOps() {
		return ops;
	}
	public void setOps(List<List<String>> ops) {
		this.ops = ops;
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
	public String getOnEnd() {
		return onEnd;
	}
	public void setOnEnd(String onEnd) {
		this.onEnd = onEnd;
	}
	public String getOnStart() {
		return onStart;
	}
	public void setOnStart(String onStart) {
		this.onStart = onStart;
	}
}
