package controllers;


import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import entities.UrlMapping;
import entities.Users;
import model.UserPrincipal;
import services.QrService;
import services.UrlCachingService;
import services.UrlMappingService;
import services.UserService;

@RestController
public class RedirectController {
	// DI and Class fields
	private final UrlMappingService urlMappingService;
	private final UrlCachingService urlCachingService;
	private final QrService qrService;
	private final UserService userService;
	
	public RedirectController(UrlMappingService urlMappingService, UrlCachingService urlCachingService, QrService qrService,
			UserService userService) {
		super();
		this.urlMappingService = urlMappingService;
		this.urlCachingService = urlCachingService;
		this.qrService = qrService;
		this.userService = userService;
		
	}
	
	@GetMapping("/{shortUrl}")
	public ResponseEntity<?> redirect(@PathVariable String shortUrl){
		String cachedOriginal =  this.urlCachingService.getOriginal(shortUrl);
		
		this.urlMappingService.recordClickEvent(shortUrl);
		
		if(cachedOriginal != null) {
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add("Location", cachedOriginal);
			return ResponseEntity.status(302).headers(httpHeaders).build(); // 302 means found and redirected
		}else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@GetMapping("/qr/{shortUrl}")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> getQr(@PathVariable String shortUrl, @AuthenticationPrincipal UserPrincipal principal ){
		try {
			
			UrlMapping urlMapping = this.urlMappingService.findByShortUrl(shortUrl);
			if(urlMapping == null) {
				return ResponseEntity.status(404).body("No Such Short Url Exists");
			}
			
			Users user = this.userService.findUserByEmail(principal.getUsername());
			
			if(!user.getId().equals(urlMapping.getUser().getId())) {
				return ResponseEntity.status(403).body("This shortUrl doesnt belong to you");
			}

			
			String redirectUrl = "https://localhost/8080/" + shortUrl; // hard coded for now chnage later
			byte [] qrImage = qrService.generateQRCode(redirectUrl, 300, 300);
			
			return ResponseEntity.ok()
					.header("Content-Disposition", "inline; filename=\"" + shortUrl + ".png\"")
					.contentType(MediaType.IMAGE_PNG)
					.body(qrImage);
		} catch (Exception e) {
			 return ResponseEntity.status(500).body(null);
		}
	}
}
