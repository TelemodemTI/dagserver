package main.domain.types;

public class LogEntry {
	private Integer id;
	private String dagname;
	private Long execDt;
	private String value;
	private String xcomoutput; 
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getDagname() {
		return dagname;
	}
	public void setDagname(String dagname) {
		this.dagname = dagname;
	}
	public Long getExecDt() {
		return execDt;
	}
	public void setExecDt(Long execDt) {
		this.execDt = execDt;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getXcomoutput() {
		return xcomoutput;
	}
	public void setXcomoutput(String xcomoutput) {
		this.xcomoutput = xcomoutput;
	}
	
}
