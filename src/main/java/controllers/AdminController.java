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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import services.UrlMappingService;
import services.UserService;

@RestController
@RequestMapping("/api/admin/users")
@Tag(
	    name = "04 - Admin User Management",
	    description = "Administrative APIs for managing users, roles, URLs, and click analytics"
	)
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
	@Operation(
	        summary = "Get User by ID",
	        description = """
	            Fetch full details of a specific user.
	            
	            Example GET request:
	            http://localhost:8080/api/admin/users/15
	            """
	    )
	public ResponseEntity<?> getUser(@PathVariable Long userId) {
		Users user = this.userService.getUserById(userId);
		return ResponseEntity.ok().body(user);
	}
	
	
	@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
	@GetMapping("/getusers")
	@Operation(
	        summary = "Get All Users",
	        description = """
	            Fetch paginated list of all users in the system.
	            
	            Example GET request:
	            http://localhost:8080/api/admin/users/getusers?page=0&size=5
	            """
	    )
	public ResponseEntity<?> getAllUsers(@RequestParam(defaultValue = "0") int page, 
			@RequestParam(defaultValue = "5") int size) {
		Page<UserDto> users = this.userService.getAllUsers(page, size);
		return ResponseEntity.ok().body(users);
	}
	
	
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/update-role")
	@Operation(
	        summary = "Update User Role",
	        description = """
	            Update role of a user. Only ADMIN can perform this operation.
	            
	            Example PUT request:
	            http://localhost:8080/api/admin/users/update-role?userId=15&roleName=MANAGER
	            """
	    )
	public ResponseEntity<?> updateUserRole(@RequestParam Long userId, @RequestParam String roleName ){
		this.userService.updateRole(userId, roleName);
		return ResponseEntity.ok().body("Role Updated Sucessfully");
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/delete/{userId}")
	@Operation(
	        summary = "Delete User",
	        description = """
	            Permanently delete a user from the system.
	            
	            Example DELETE request:
	            http://localhost:8080/api/admin/users/delete/15
	            """
	    )
	public ResponseEntity<?> deleteUser(@PathVariable Long userId){
		this.userService.deleteUser(userId);
		return ResponseEntity.ok().body("User with userId: " + userId + ", deleted sucessfully");
	}
	
	
	@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
	@GetMapping("/url-mappings")
	@Operation(
	        summary = "Get URL Mappings by User",
	        description = """
	            Fetch all shortened URLs created by a specific user.
	            
	            Example GET request:
	            http://localhost:8080/api/admin/users/url-mappings?userId=15&page=0&size=5
	            """
	    )
	public ResponseEntity<?> getUrlMappingByUser(@RequestParam Long userId, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size){
		Page<UrlMappingDto> urls =  this.urlMappingService.getUrlMappingsByUser(userId, page, size);
		
		return ResponseEntity.ok().body(urls);
	}
	
	@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
	@GetMapping("/click-events") 
	@Operation(
	        summary = "Get Click Events by URL",
	        description = """
	            Fetch paginated click events for a specific URL.
	            
	            Example GET request:
	            http://localhost:8080/api/admin/users/click-events?urlId=22&page=0&size=5
	            """
	    )
	public ResponseEntity<?> getClickEventsByUrlId(@RequestParam Long urlId, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size){
		
		Page<ClickEvent> clickEvents =  this.urlMappingService.getClickEventsByUrlId(urlId, page, size);
		
		return ResponseEntity.ok().body(clickEvents);
	}
}
