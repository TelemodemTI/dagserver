package main.cl.dagserver.infra.adapters.input.graphql.types;



public class Exceptions {

	private String eventDt;
    private String classname;
    private String method;
    private String stack;
	public String getEventDt() {
		return eventDt;
	}
	public void setEventDt(String eventDt) {
		this.eventDt = eventDt;
	}
	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getStack() {
		return stack;
	}
	public void setStack(String stack) {
		this.stack = stack;
	}
    	
}
