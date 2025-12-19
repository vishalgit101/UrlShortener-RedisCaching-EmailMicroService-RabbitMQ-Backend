package entities;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "click_event")
public class ClickEvent {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "click_date")
	private LocalDateTime clickDate;
	
	@Column(name = "user_ip")
	private String userIp;
	
	@Column(name = "user_agent")
	private String userAgent;
	
	@Column(name = "accept_language")
	private String acceptLanguage;
	
	@Column(name = "endpoint")
	private String endpoint;
	
	@ManyToOne
	@JoinColumn(name = "url_mapping_id")
	@JsonIgnore
	private UrlMapping urlMapping;
	
	public ClickEvent() {
		// TODO Auto-generated constructor stub
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getClickDate() {
		return clickDate;
	}

	public void setClickDate(LocalDateTime clickDate) {
		this.clickDate = clickDate;
	}
	
	public String getUserIp() {
		return userIp;
	}

	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getAcceptLanguage() {
		return acceptLanguage;
	}

	public void setAcceptLanguage(String acceptLanguage) {
		this.acceptLanguage = acceptLanguage;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public UrlMapping getUrlMapping() {
		return urlMapping;
	}

	public void setUrlMapping(UrlMapping urlMapping) {
		this.urlMapping = urlMapping;
	}
	
	
}
