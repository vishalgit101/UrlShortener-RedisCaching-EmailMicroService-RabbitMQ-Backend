package controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dtos.UserDto;
import entities.Users;
import model.UserPrincipal;
import services.UserService;

@RestController
@RequestMapping("/api/auth/user")
public class UserController {
	private final UserService userService;

	public UserController(UserService userService) {
		super();
		this.userService = userService;
	}
	
	@GetMapping
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> getUser(@AuthenticationPrincipal UserPrincipal principal){
		Users user = this.userService.findUserByEmail(principal.getUsername());
		UserDto userDto = this.userService.getUserDto(user);
		
		return ResponseEntity.ok().body(userDto);
	}
	
	
	// more controller methods related to user like updating credentials or profile etc could also come here when later decided
	
}
