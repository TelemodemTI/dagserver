package main.cl.dagserver.application.pojos;

import java.util.Date;
import java.util.Map;

import com.nhl.dflib.DataFrame;

public class LogPOJO {
	private Integer id;
	private String dagname;
	private Date execDt;
	private String value;
	private String outputxcom;
	private Map<String,DataFrame> xcom;
	private String marks;
	private String status;
	private String channel;
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
	public Date getExecDt() {
		return execDt;
	}
	public void setExecDt(Date execDt) {
		this.execDt = execDt;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getOutputxcom() {
		return outputxcom;
	}
	public void setOutputxcom(String outputxcom) {
		this.outputxcom = outputxcom;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getMarks() {
		return marks;
	}
	public void setMarks(String marks) {
		this.marks = marks;
	}
	public Map<String, DataFrame> getXcom() {
		return xcom;
	}
	public void setXcom(Map<String, DataFrame> xcom) {
		this.xcom = xcom;
	}

}
