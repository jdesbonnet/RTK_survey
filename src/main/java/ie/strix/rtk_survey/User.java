package ie.strix.rtk_survey;

import java.beans.Transient;
import java.util.Date;
import javax.persistence.Entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//@Entity
public class User {
	
	private static Logger log = LoggerFactory.getLogger(User.class);

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;


	private String name;
	
	private String email;
	
	private String password;
	
	private Date lastLogin = null;



	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	
	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}
	
	
	/**
	 * Temporary method to confer super-admin privileges on some users. SuperAdmin
	 * can create new DB instances.
	 * 
	 * @return
	 */
	@Transient	
	public boolean isSuperAdmin() {
		return ("jdesbonnet@gmail.com".equals(getEmail()));
	}
	

}
