package controllers;


import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import entities.UrlMapping;
import services.UrlMappingService;

@RestController
public class RedirectController {
	// DI and Class fields
	private final UrlMappingService urlMappingService;

	public RedirectController(UrlMappingService urlMappingService) {
		super();
		this.urlMappingService = urlMappingService;
	}
	
	@GetMapping("/{shortUrl}")
	public ResponseEntity<?> redirect(@PathVariable String shortUrl){
		UrlMapping urlMapping = this.urlMappingService.getOriginalUrl(shortUrl);
		
		if(urlMapping != null) {
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add("Location", urlMapping.getOriginalUrl());
			return ResponseEntity.status(302).headers(httpHeaders).build(); // 302 means found and redirected
		}else {
			return ResponseEntity.notFound().build();
		}
	}
}
