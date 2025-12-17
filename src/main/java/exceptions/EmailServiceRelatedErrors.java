package exceptions;

public class EmailServiceRelatedErrors extends RuntimeException {
	public EmailServiceRelatedErrors(String message) {
		super(message);
	}
}
