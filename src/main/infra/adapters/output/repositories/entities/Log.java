package main.infra.adapters.output.repositories.entities;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Type;


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
	@Type(type="timestamp")
	private Date execDt;
	 
	@Column(name = "text_value", nullable = false)
	private String value;
	
	@Column(name = "outxcom", nullable = true)
	private String outputxcom;

	@Column(name = "status", nullable = false)
	private String status;
	
	@Column(name = "channel", nullable = false)
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

}
