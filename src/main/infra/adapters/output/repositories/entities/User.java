package main.infra.adapters.output.repositories.entities;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import main.application.pojos.UserPOJO;

@Entity
@Table(name="sche_users")
public class User extends UserPOJO {
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
	@Type(type="timestamp")
	private Date createdAt;

	
}
