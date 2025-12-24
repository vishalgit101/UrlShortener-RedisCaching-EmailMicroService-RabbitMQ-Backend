package controllers;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dtos.UserDto;
import entities.ClickEvent;
import entities.UrlMapping;
import entities.Users;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import model.UserPrincipal;
import services.UrlMappingService;
import services.UserService;

@RestController
@RequestMapping("/api/auth/user")
@Tag(
	name = "03 - User",
	description = "APIs related to authenticated user profile and activity"
		)
public class UserController {
	private final UserService userService;
	private final UrlMappingService urlMappingService;
	
	public UserController(UserService userService, UrlMappingService urlMappingService) {
		super();
		this.userService = userService;
		this.urlMappingService = urlMappingService;
	}
	
	@GetMapping
	@PreAuthorize("hasRole('USER')")
	@Operation(
	        summary = "Get Logged-in User Details",
	        description = """
	            Returns profile information of the currently authenticated user.
	            
	            Example GET request:
	            http://localhost:8080/api/auth/user
	            """
	    )
	public ResponseEntity<?> getUser(@AuthenticationPrincipal UserPrincipal principal){
		Users user = this.userService.findUserByEmail(principal.getUsername());
		UserDto userDto = this.userService.getUserDto(user);
		
		return ResponseEntity.ok().body(userDto);
	}
	

	@Operation(
	        summary = "Get Click Events for a URL",
	        description = """
	            Fetches paginated click events for a specific URL owned by the authenticated user.
	            
	            Example GET request:
	            http://localhost:8080/api/auth/user/url/click-events?urlId=12&page=0&size=5
	            """
	    )
	@GetMapping("/url/click-events")
	public ResponseEntity<?> getClickEventsByUrlId(@RequestParam Long urlId, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size, @AuthenticationPrincipal UserPrincipal principal){
		
		UrlMapping urlMapping = this.urlMappingService.getUrlMappingByUrlId(urlId);
		
		Users user = this.userService.findUserByEmail(principal.getUsername());
		
		if(!urlMapping.getUser().getId().equals(user.getId())) {
			return ResponseEntity.ok().body("Url with urlId: " + urlId + ", doesnt belong to you");
		}
		
		Page<ClickEvent> clickEvents =  this.urlMappingService.getClickEventsByUrlId(urlId, page, size);
		
		return ResponseEntity.ok().body(clickEvents);
	}
	
	
	// more controller methods related to user like updating credentials or profile etc could also come here when later decided
	
}
