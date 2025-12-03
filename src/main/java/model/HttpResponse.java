package model;

import java.util.Map;

import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class HttpResponse {
	protected String timeStamp;
	protected int statuscode;
	protected HttpStatus httpStatus;
	protected String message;
	protected String developerMessage;
	protected String path; // The URL path that was called.
	protected String requestedMethod; // for sending that back as well, Method GET, POST which was used
	protected Map<?,?> user;
	
	public HttpResponse() {
		// TODO Auto-generated constructor stub
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public int getStatuscode() {
		return statuscode;
	}

	public void setStatuscode(int statuscode) {
		this.statuscode = statuscode;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDeveloperMessage() {
		return developerMessage;
	}

	public void setDeveloperMessage(String developerMessage) {
		this.developerMessage = developerMessage;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getRequestedMethod() {
		return requestedMethod;
	}

	public void setRequestedMethod(String requestedMethod) {
		this.requestedMethod = requestedMethod;
	}

	public Map<?, ?> getUser() {
		return user;
	}

	public void setUser(Map<?, ?> user) {
		this.user = user;
	}
	
	
}
