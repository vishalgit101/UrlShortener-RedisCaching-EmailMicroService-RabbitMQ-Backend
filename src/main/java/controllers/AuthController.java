package controllers;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dtos.PasswordResetRequestDto;
import dtos.UserLoginDTO;
import dtos.UserRegisterDTO;
import entities.Users;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import model.HttpResponse;
import model.UserPrincipal;
import services.UserService;

@RestController
@RequestMapping("/api/auth")
@Tag(
	    name = "01 - Authentication & Authorization",
	    description = "Handles user registration, email verification, login, and password reset operations"
	)

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
	
	@GetMapping("/public/health")
	public String health() {
		return "Running";
	}

	@Operation(
		    summary = "01 Register a new user",
		    description = "Creates a new user account and sends an email verification link via asynchronous email service"
		)
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
	
	@Operation(
		    summary = "03 Authenticate user",
		    description = "Validates user credentials and returns a JWT token upon successful authentication"
		)
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
	
	@Operation(
			//operationId = "02_verifyUser",
		    summary = "02 Verify user account",
		    description = "Verifies user account using the email confirmation token"
		)
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
	
	
	@Operation(
		    summary = "04 Request password reset",
		    description = "Sends a password reset link to the registered email address"
		)
	@PostMapping("/public/reset-password")
	public ResponseEntity<?> resetPassword(@RequestParam("email")  String email) {
		this.userService.resetPassword(email);
		
		return ResponseEntity.ok().body("Please check your email for password reset link");
	}
	
	@Operation(
		    summary = "05 Reset password",
		    description = "Resets the user password using a valid password reset token, COPY the TOKEN FROM your EMAIL"
		)
	@PutMapping("/public/reset-password")
	public ResponseEntity<?> updatePassword(@RequestBody PasswordResetRequestDto passwordReset){
		if(!passwordReset.getNewPassword().equals(passwordReset.getConfirmPassword())) {
			ResponseEntity.badRequest().body("Your password and confirmation password dont match");
		}
		boolean success = this.userService.verifyPasswordResetToken(passwordReset);
		
		if(success) {
			return ResponseEntity.ok().body("Password got reset sucessfully");
		}else {
			return ResponseEntity.ok().body("Password reset request failed for some reason");
		}
		
	}
}
