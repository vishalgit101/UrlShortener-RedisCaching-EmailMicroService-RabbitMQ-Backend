package controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dtos.ClickEventDto;
import dtos.UrlDto;
import dtos.UrlMappingDto;
import entities.UrlMapping;
import entities.Users;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import model.UserPrincipal;
import services.UrlMappingService;
import services.UserService;

@RestController
@RequestMapping("/api/urls")
@Tag(
		name = "02 - URL Management", 
		description = "APIs to create, fetch, analyze, and delete shortened URLs"
)
public class UrlMappingController {
	private UrlMappingService urlMappingService;
	private UserService userService;
	
	public UrlMappingController(UrlMappingService urlMappingService, UserService userService) {
		super();
		this.urlMappingService = urlMappingService;
		this.userService = userService;
	}
	
	// {"originalUrl":"https://example.com"}
	
	@PostMapping("/shorten")
	@PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Create Short URL",
            description = "Creates a shortened URL for the given original URL and stores in the redis cache. User must be authenticated."
        )
	public ResponseEntity<?> createShortUrl(@RequestBody UrlDto request, @AuthenticationPrincipal UserPrincipal principal) {
		String originalUrl = request.getOriginalUrl();
		Users user = this.userService.findUserByEmail(principal.getUsername());
		UrlMappingDto urlMappingDto = this.urlMappingService.createShortUrl(originalUrl, user);
		return ResponseEntity.ok().body(urlMappingDto);
	}
	
	// fetching all the urls of a user
	
	@GetMapping("/myUrls")
	@PreAuthorize("hasRole('USER')")
	@Operation(
		        summary = "Fetch User URLs",
		        description = "Fetches all shortened URLs created by the authenticated user.\n" +
		                      "Example GET request:\n" +
		                      "http://localhost:8080/api/urls/myUrls?page=0"
		    )
	public ResponseEntity<?> fetchAllUrlByUser(@AuthenticationPrincipal UserPrincipal principal, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size){
		Users user = this.userService.findUserByEmail(principal.getUsername());
		
		//List<UrlMappingDto> urls = this.urlMappingService.getUrlsByUser(user);
		Page<UrlMappingDto> urls = this.urlMappingService.getUrlsByUser(user, page, size);
				
		return ResponseEntity.ok().body(urls);
	}
	
	@GetMapping("/analytics/{shortUrl}") // PathVariable
	@PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Get URL Analytics",
            description = "Fetches click events for a specific short URL in a date-time range.\n" +
                          "Example GET request:\n" +
                          "http://localhost:8080/api/urls/analytics/Es5hGE?startDate=2024-12-01T00:00:00&endDate=2026-12-07T23:59:59"
        )
	public ResponseEntity<?> getUrlAnalytics(@PathVariable String shortUrl, 
			@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate, @AuthenticationPrincipal UserPrincipal principal){
		
		Users user = this.userService.findUserByEmail(principal.getUsername());
		
		UrlMapping urlMapping = this.urlMappingService.findByShortUrl(shortUrl);
		
		if(urlMapping == null) {
			return ResponseEntity.status(404).body("No Such Short Url Exists in the dB");
		}
		
		if(!user.getId().equals(urlMapping.getUser().getId())) {
			return ResponseEntity.status(403).body("This shortUrl doesnt belong to you");
		}
		
		DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
		LocalDateTime start = LocalDateTime.parse(startDate, formatter);
		LocalDateTime end = LocalDateTime.parse(endDate, formatter);
		List<ClickEventDto> clickEventDtos = this.urlMappingService.getClickEventsByDate(shortUrl, start, end);
		return ResponseEntity.ok().body(clickEventDtos);
	}
	
	@GetMapping("/totalClicks")
	@PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Get Total Clicks",
            description = "Returns total clicks per day for the authenticated user in a date range.\n" +
                          "Example GET request:\n" +
                          "http://localhost:8080/api/urls/totalClicks?startDate=2024-12-01&endDate=2026-12-07"
        )
	public ResponseEntity<?> getTotalClicksByDate(@AuthenticationPrincipal UserPrincipal principal, 
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate
			){
		DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
		Users user = this.userService.findUserByEmail(principal.getUsername());
		LocalDate start = LocalDate.parse(startDate, formatter);
		LocalDate end = LocalDate.parse(endDate, formatter);
		Map<LocalDate, Integer> totalClicks = this.urlMappingService.getTotalClicksByUserAndDate(user, start, end);
		return ResponseEntity.ok().body(totalClicks);
	}
	
	// Delete Mapping
	@DeleteMapping("/delete/{shortUrl}")
	@PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Delete Short URL",
            description = "Deletes the short URL and all associated click data for the authenticated user.\n" +
                          "Example DELETE request:\n" +
                          "http://localhost:8080/api/urls/delete/Es5hGE"
        )
	public ResponseEntity<?> deleteMapping(@PathVariable String shortUrl, @AuthenticationPrincipal UserPrincipal principal){
		Users user = this.userService.findUserByEmail(principal.getUsername());
		
		UrlMapping urlMapping = this.urlMappingService.findByShortUrl(shortUrl);
		
		if(urlMapping == null) {
			return ResponseEntity.status(404).body("No Such Short Url Exists in the dB");
		}
		
		if(!user.getId().equals(urlMapping.getUser().getId())) {
			return ResponseEntity.status(403).body("This shortUrl doesnt belong to you");
		}
		
		this.urlMappingService.deleteUrl(urlMapping);
		
		return ResponseEntity.ok().body("All Data Related to " + shortUrl + ", Deleted Sucessfully");
	}
}
