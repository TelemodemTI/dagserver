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
@Table(name="sche_users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="user_id",nullable=false)
	private Integer id;
	 
	@Column(name = "username", nullable = false)
	private String username;
	
	@Column(name = "pwdhash", nullable = false)
	private String pwdhash;

	@Column(name = "type_account", nullable = false)
	private String typeAccount;
	
	@Column(name="created_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPwdhash() {
		return pwdhash;
	}
	public void setPwdhash(String pwdhash) {
		this.pwdhash = pwdhash;
	}
	public String getTypeAccount() {
		return typeAccount;
	}
	public void setTypeAccount(String typeAccount) {
		this.typeAccount = typeAccount;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
}
