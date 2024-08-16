package main.cl.dagserver.infra.adapters.output.repositories.entities;
import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;



@Entity
@Table(name="sche_logs")
public class Log {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="log_id",nullable=false)
	private Integer id;
	 
	@Column(name = "dagname", nullable = false)
	private String dagname;
	
	@Column(name = "evalkey", nullable = false)
	private String evalkey;

	@Column(name="exec_dt")
	@Temporal(TemporalType.TIMESTAMP)
	private Date execDt;
	 
	@Column(name = "text_value", nullable = false)
	private String value;
	
	@Column(name = "outxcom", nullable = true)
	private String outputxcom;

	@Column(name = "marks", nullable = false)
	private String marks;
	
	@Column(name = "status", nullable = false)
	private String status;
	
	
	@Column(name = "channel", nullable = false)
	private String channel;
	
	@Column(name = "source_type", nullable = true)
	private String sourceType;
	
	@Column(name = "objetive", nullable = true)
	private String objetive;
	
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
	public String getEvalkey() {
		return evalkey;
	}
	public void setEvalkey(String evalkey) {
		this.evalkey = evalkey;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getSourceType() {
		return sourceType;
	}
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	public String getObjetive() {
		return objetive;
	}
	public void setObjetive(String objetive) {
		this.objetive = objetive;
	}
	public String getMarks() {
		return marks;
	}
	public void setMarks(String marks) {
		this.marks = marks;
	}

}
