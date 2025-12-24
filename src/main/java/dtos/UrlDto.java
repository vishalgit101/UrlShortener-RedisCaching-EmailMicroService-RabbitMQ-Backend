package dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UrlRequest", description = "Request object to create a shortened URL")
public class UrlDto {
    @Schema(
            description = "The original URL to shorten",
            example = "https://example.com",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
	private String originalUrl;
	
	public UrlDto() {
		// TODO Auto-generated constructor stub
	}

	public String getOriginalUrl() {
		return originalUrl;
	}

	public void setOriginalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
	}
	
	
}
