package dtos;

import java.util.Set;


public class UserDto {
	private Long id;
	
	private String fullName;
	
	private String email;
	
	private Set<String> roles;
	
	private boolean verified;

	public UserDto() {
		super();
	}

	public UserDto(Long id, String fullName, String email, Set<String> roles, boolean verified) {
		super();
		this.id = id;
		this.fullName = fullName;
		this.email = email;
		this.roles = roles;
		this.verified = verified;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}
	
	
}
