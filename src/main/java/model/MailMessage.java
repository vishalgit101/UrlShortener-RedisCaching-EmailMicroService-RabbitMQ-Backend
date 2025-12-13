package model;

import java.time.LocalDateTime;

public class MailMessage {
	private String id;
	
	private String fullname;
	
	private String email;
	
	private String token;
	
	private String priority;
	
	private LocalDateTime createdAt;

	public MailMessage() {
		
	}

	public MailMessage(String id, String fullname, String email, String token, String priority,
			LocalDateTime createdAt) {
		super();
		this.id = id;
		this.fullname = fullname;
		this.email = email;
		this.token = token;
		this.priority = priority;
		this.createdAt = createdAt;
	}
	
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	
	
}
