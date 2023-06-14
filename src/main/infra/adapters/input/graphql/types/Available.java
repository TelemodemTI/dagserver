package main.infra.adapters.input.graphql.types;

public class Available {
	
	public String jarname;
	public String classname;
	public String groupname;
	public String dagname;
	public String cronExpr;
	public String triggerEvent;
	public String targetDagname;
	
	
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
