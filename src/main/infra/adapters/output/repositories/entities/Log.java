package main.infra.adapters.output.repositories.entities;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import main.application.pojos.LogPOJO;

@Entity
@Table(name="sche_logs")
public class Log extends LogPOJO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="log_id",nullable=false)
	private Integer id;
	 
	@Column(name = "dagname", nullable = false)
	private String dagname;
	 
	@Column(name="exec_dt")
	@Type(type="timestamp")
	private Date execDt;
	 
	@Column(name = "text_value", nullable = false)
	private String value;
	
	@Column(name = "outxcom", nullable = false)
	private String outputxcom;

	@Column(name = "status", nullable = false)
	private String status;
	
	
}
