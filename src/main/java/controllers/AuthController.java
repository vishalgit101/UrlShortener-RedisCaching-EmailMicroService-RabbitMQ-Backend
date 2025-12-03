package controllers;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dtos.UserLoginDTO;
import dtos.UserRegisterDTO;
import entities.Users;
import jakarta.servlet.http.HttpServletRequest;
import model.HttpResponse;
import model.UserPrincipal;
import services.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	// DI and class fields
	private final UserService userService;

	public AuthController(UserService userService) {
		super();
		this.userService = userService;
	}
	
	@GetMapping("/test")
	public String hello( @AuthenticationPrincipal UserPrincipal principal) {
		System.out.println(principal.getUsername());
		System.out.println(principal.getAuthorities());
		System.out.println("hit");
		return "Hello";
	}

	@PostMapping("/public/register")
	public ResponseEntity<HttpResponse> register(@RequestBody UserRegisterDTO dto, HttpServletRequest request) {
		System.out.println("Hit");
		Users savedUser = this.userService.registerUser(dto);
		
		HttpResponse response = new HttpResponse();
		response.setHttpStatus(HttpStatus.CREATED);
		response.setStatuscode(HttpStatus.CREATED.value());
		response.setTimeStamp(LocalDateTime.now().toString());
		response.setMessage("User account created successfully, please verify it");
		response.setDeveloperMessage("After verification you can start shortening urls");
		response.setPath(request.getRequestURI());
		response.setRequestedMethod(request.getMethod());
		response.setUser(Map.of("email", savedUser.getEmail()));
		
		//return ResponseEntity.created(URI.create("/api/users/" + savedUser.getId())).body(response);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@PostMapping("/public/login")
	public ResponseEntity<HttpResponse> login(@RequestBody UserLoginDTO userlogin, HttpServletRequest request) {
		String jwtToken = this.userService.userLoginVerification(userlogin);
		
		HttpResponse response = new HttpResponse();
		response.setHttpStatus(HttpStatus.OK);
		response.setStatuscode(HttpStatus.OK.value());
		response.setTimeStamp(LocalDateTime.now().toString());
		response.setMessage("Login Successful");
		response.setDeveloperMessage("Login Sucessful");
		response.setPath(request.getRequestURI());
		response.setRequestedMethod(request.getMethod());
		response.setUser(Map.of("token", jwtToken));
		
		return ResponseEntity.ok().body(response);
	}
	
	@GetMapping("/public/verify")
	public ResponseEntity<HttpResponse> verifyUser(@RequestParam String token, HttpServletRequest request){
		
		boolean result = this.userService.userConfirmation(token);
		HttpResponse response = new HttpResponse();
		
		if(result) {
			response.setHttpStatus(HttpStatus.OK);
			response.setStatuscode(HttpStatus.OK.value());
			response.setTimeStamp(LocalDateTime.now().toString());
			response.setMessage("Account Sucessfully Verified!");
			response.setDeveloperMessage("Account Sucessfully Verified, please login now");
			response.setPath(request.getRequestURI());
			response.setRequestedMethod(request.getMethod());
			
			return ResponseEntity.status(HttpStatus.OK).body(response);
		}else {
			response.setHttpStatus(HttpStatus.BAD_REQUEST);
			response.setStatuscode(HttpStatus.BAD_REQUEST.value());
			response.setTimeStamp(LocalDateTime.now().toString());
			response.setMessage("Invalid or Expired token");
			response.setDeveloperMessage("Invalid");
			response.setPath(request.getRequestURI());
			response.setRequestedMethod(request.getMethod());
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		
		
	}
}
