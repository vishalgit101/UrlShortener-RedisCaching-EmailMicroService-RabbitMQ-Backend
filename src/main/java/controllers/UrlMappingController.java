package controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dtos.ClickEventDto;
import dtos.UrlMappingDto;
import entities.Users;
import model.UserPrincipal;
import services.UrlMappingService;
import services.UserService;

@RestController
@RequestMapping("/api/urls")
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
	public ResponseEntity<?> createShortUrl(@RequestBody Map<String, String> request,@AuthenticationPrincipal UserPrincipal principal) {
		String originalUrl = request.get("originalUrl");
		Users user = this.userService.findUserByEmail(principal.getUsername());
		UrlMappingDto urlMappingDto = this.urlMappingService.createShortUrl(originalUrl, user);
		return ResponseEntity.ok().body(urlMappingDto);
	}
	
	// fetching all the urls of a user
	
	@GetMapping("/myUrls")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> fetchAllUrlByUser(@AuthenticationPrincipal UserPrincipal principal, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size){
		Users user = this.userService.findUserByEmail(principal.getUsername());
		
		//List<UrlMappingDto> urls = this.urlMappingService.getUrlsByUser(user);
		Page<UrlMappingDto> urls = this.urlMappingService.getUrlsByUser(user, page, size);
				
		return ResponseEntity.ok().body(urls);
	}
	
	@GetMapping("/analytics/{shortUrl}") // PathVariable
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> getUrlAnalytics(@PathVariable String shortUrl, 
			@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate){
		
		DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
		LocalDateTime start = LocalDateTime.parse(startDate, formatter);
		LocalDateTime end = LocalDateTime.parse(endDate, formatter);
		List<ClickEventDto> clickEventDtos = this.urlMappingService.getClickEventsByDate(shortUrl, start, end);
		return ResponseEntity.ok().body(clickEventDtos);
	}
	
	@GetMapping("/totalClicks")
	@PreAuthorize("hasRole('USER')")
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
	
}
