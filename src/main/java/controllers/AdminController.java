package controllers;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dtos.UrlMappingDto;
import dtos.UserDto;
import entities.ClickEvent;
import entities.Users;
import services.UrlMappingService;
import services.UserService;

@RestController
@RequestMapping("/api/admin/users")
public class AdminController {
	
	private final UserService userService;
	private final UrlMappingService urlMappingService;
	
	public AdminController(UserService userService, UrlMappingService urlMappingService) {
		super();
		this.userService = userService;
		this.urlMappingService = urlMappingService;
	}
	
	@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
	@GetMapping("/{userId}") // Admin can access everything about particular user
	public ResponseEntity<?> getUser(@PathVariable Long userId) {
		Users user = this.userService.getUserById(userId);
		return ResponseEntity.ok().body(user);
	}
	
	@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
	@GetMapping("/getusers")
	public ResponseEntity<?> getAllUsers(@RequestParam(defaultValue = "0") int page, 
			@RequestParam(defaultValue = "5") int size) {
		Page<UserDto> users = this.userService.getAllUsers(page, size);
		return ResponseEntity.ok().body(users);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/update-role")
	public ResponseEntity<?> updateUserRole(@RequestParam Long userId, @RequestParam String roleName ){
		this.userService.updateRole(userId, roleName);
		return ResponseEntity.ok().body("Role Updated Sucessfully");
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/delete/{userId}")
	public ResponseEntity<?> deleteUser(@PathVariable Long userId){
		this.userService.deleteUser(userId);
		return ResponseEntity.ok().body("User with userId: " + userId + ", deleted sucessfully");
	}
	
	
	@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
	@GetMapping("/url-mappings")
	public ResponseEntity<?> getUrlMappingByUser(@RequestParam Long userId, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size){
		Page<UrlMappingDto> urls =  this.urlMappingService.getUrlMappingsByUser(userId, page, size);
		
		return ResponseEntity.ok().body(urls);
	}
	
	@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
	@GetMapping("/click-events") 
	public ResponseEntity<?> getClickEventsByUrlId(@RequestParam Long urlId, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size){
		
		Page<ClickEvent> clickEvents =  this.urlMappingService.getClickEventsByUrlId(urlId, page, size);
		
		return ResponseEntity.ok().body(clickEvents);
	}
}
