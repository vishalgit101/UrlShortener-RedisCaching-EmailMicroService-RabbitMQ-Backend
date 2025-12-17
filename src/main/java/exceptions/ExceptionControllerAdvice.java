package exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import model.ErrorDetails;

@RestControllerAdvice // for global exception handling
public class ExceptionControllerAdvice {
	
	// This one could be avoided and RecourseConflict could be used instead
	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<ErrorDetails> userAlreadyExistsHandler(UserAlreadyExistsException ex, WebRequest request){
		
		ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(),
				request.getDescription(false), // e.g., uri=/api/myUrls/100
				"CONFLICT_ERROR");
		//return ResponseEntity.badRequest().body(ex.getMessage());
		return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
		
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request){
		
		ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(),
				request.getDescription(false), // e.g., uri=/api/myUrls/100
				"NOT_FOUND_ERROR");
		
		return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(ResourceConflictException.class)
	public ResponseEntity<ErrorDetails> handleResourceConflictException(ResourceConflictException ex, WebRequest request){
		
		ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), 
				request.getDescription(false), "CONFLICT_ERROR");
		
		return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
				
	}
	
	@ExceptionHandler(TokenRelatedException.class)
	public ResponseEntity<ErrorDetails> handleTokenRelatedException(TokenRelatedException ex, WebRequest request){
		
		ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), 
				request.getDescription(false), "BAD_REQUEST");
		
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
				
	}
	
	@ExceptionHandler(EmailServiceRelatedErrors.class)
	public ResponseEntity<ErrorDetails> handlEMailSericeRelatedException(EmailServiceRelatedErrors ex, WebRequest request){
		
		ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), 
				request.getDescription(false), "SERVICE_UNAVAILABLE");
		
		return new ResponseEntity<>(errorDetails, HttpStatus.SERVICE_UNAVAILABLE);
				
	}
}
