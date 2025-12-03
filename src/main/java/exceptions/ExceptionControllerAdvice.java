package exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // for global exception handling
public class ExceptionControllerAdvice {
	
	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<String> userAlreadyExistsHandler(UserAlreadyExistsException ex){
		return ResponseEntity.badRequest().body(ex.getMessage());
	}
}
