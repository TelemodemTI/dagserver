package main.cl.dagserver.infra.adapters.input.graphql.types;

public class Available {
	
	private String jarname;
	private String classname;
	private String groupname;
	private String dagname;
	private String cronExpr;
	private String triggerEvent;
	private String targetDagname;
	
	
	public String getJarname() {
		return jarname;
	}
	public void setJarname(String jarname) {
		this.jarname = jarname;
	}
	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
	public String getGroupname() {
		return groupname;
	}
	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}
	public String getDagname() {
		return dagname;
	}
	public void setDagname(String dagname) {
		this.dagname = dagname;
	}
	public String getCronExpr() {
		return cronExpr;
	}
	public void setCronExpr(String cronExpr) {
		this.cronExpr = cronExpr;
	}
	public String getTriggerEvent() {
		return triggerEvent;
	}
	public void setTriggerEvent(String triggerEvent) {
		this.triggerEvent = triggerEvent;
	}
	public String getTargetDagname() {
		return targetDagname;
	}
	public void setTargetDagname(String targetDagname) {
		this.targetDagname = targetDagname;
	}
	
}
