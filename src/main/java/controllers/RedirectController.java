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
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import model.UserPrincipal;
import services.QrService;
import services.UrlCachingService;
import services.UrlMappingService;
import services.UserService;

@RestController
@Tag(
	    name = "05 - Redirect & QR",
	    description = "Browser redirection and QR code generation for shortened URLs"
	)
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
	@Hidden
	public ResponseEntity<?> redirect(@PathVariable String shortUrl, HttpServletRequest request){
		//HttpServletRequest request is thread bound and after controller returns this request object might become unavailable for the Async functions/methods that's a problem

		String cachedOriginal =  this.urlCachingService.getOriginal(shortUrl);
		
		String clientIp = extractClientIp(request);
		String userAgent = request.getHeader("User-Agent");
		String language = request.getHeader("Accept-Language");
		String endpoint = request.getRequestURI();
		
		this.urlMappingService.recordClickEvent(shortUrl,clientIp, userAgent, language, endpoint); // Async function inside the service
		
		if(cachedOriginal != null) {
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add("Location", cachedOriginal);
			return ResponseEntity.status(302).headers(httpHeaders).build(); // 302 means found and redirected
		}else {
			return ResponseEntity.notFound().build();
		}
	}
	
	// helper method to extract clientIP
	public String extractClientIp(HttpServletRequest request) {
		String xff = request.getHeader("X-Forwarded-For"); // contains the ip list that a rerquest may passed with differnt proxies on the server
		
		if(xff != null && !xff.isEmpty()) {
			return xff.split(",")[0].trim(); // first one is the client's ip
		}
		
		return request.getRemoteAddr();
	}
	
	
	// for generating the qr code for a specific url that's already in the dB
	@GetMapping("/qr/{shortUrl}")
	@PreAuthorize("hasRole('USER')")
	@Operation(
	        summary = "Generate QR Code for Short URL",
	        description = """
	            Generates a QR code image for an existing short URL.
	            Only the owner of the URL can generate its QR code.
	            
	            The QR code points to the public redirect URL.
	            
	            Example GET request:
	            http://localhost:8080/qr/Es5hGE
	            
	            Response:
	            PNG image (300x300)
	            """
	    )
	public ResponseEntity<?> getQr(@PathVariable String shortUrl, @AuthenticationPrincipal UserPrincipal principal ){
		try {
			
			UrlMapping urlMapping = this.urlMappingService.findByShortUrl(shortUrl);
			if(urlMapping == null) {
				return ResponseEntity.status(404).body("No Such Short Url Exists in the dB");
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
