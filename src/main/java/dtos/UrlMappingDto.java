package dtos;

import java.time.LocalDateTime;



public class UrlMappingDto {
	private Long id;

	private int clickCount;

	private String originalUrl;

	private String shortUrl;

	private LocalDateTime createDate;

	private String username; // useremail
	
	private String acessUrl;
	
	public UrlMappingDto() {
		// TODO Auto-generated constructor stub
	}

	public UrlMappingDto(Long id, int clickCount, String originalUrl, String shortUrl, LocalDateTime createDate,
			String username, String acessUrl) {
		super();
		this.id = id;
		this.clickCount = clickCount;
		this.originalUrl = originalUrl;
		this.shortUrl = shortUrl;
		this.createDate = createDate;
		this.username = username;
		this.acessUrl = acessUrl;
	}
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getClickCount() {
		return clickCount;
	}

	public void setClickCount(int clickCount) {
		this.clickCount = clickCount;
	}

	public String getOriginalUrl() {
		return originalUrl;
	}

	public void setOriginalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
	}

	public String getShortUrl() {
		return shortUrl;
	}

	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}

	public LocalDateTime getCreateDate() {
		return createDate;
	}

	public void setCreateDate(LocalDateTime createDate) {
		this.createDate = createDate;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAcessUrl() {
		return acessUrl;
	}

	public void setAcessUrl(String acessUrl) {
		this.acessUrl = acessUrl;
	}
	
}
