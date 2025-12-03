package entities;

import java.time.LocalDateTime;

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
	
	@ManyToOne
	@JoinColumn(name = "url_mapping_id")
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

	public UrlMapping getUrlMapping() {
		return urlMapping;
	}

	public void setUrlMapping(UrlMapping urlMapping) {
		this.urlMapping = urlMapping;
	}
	
	
}
