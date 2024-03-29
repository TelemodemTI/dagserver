package main.cl.dagserver.infra.adapters.output.repositories.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="sche_uncompiled_dags")
public class ScheUncompiledDags {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="uncompiled_id",nullable=false)
	private Integer uncompiledId;
	
	
	@Column(name = "uncompiled_name", nullable = false)
	private String name;
	
	@Column(name = "bin", nullable = false)
	private String bin;
	
	@Column(name="created_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDt;

	public Integer getUncompiledId() {
		return uncompiledId;
	}

	public void setUncompiledId(Integer uncompiledId) {
		this.uncompiledId = uncompiledId;
	}

	public String getBin() {
		return bin;
	}

	public void setBin(String bin) {
		this.bin = bin;
	}

	public Date getCreatedDt() {
		return createdDt;
	}

	public void setCreatedDt(Date createdDt) {
		this.createdDt = createdDt;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
}
