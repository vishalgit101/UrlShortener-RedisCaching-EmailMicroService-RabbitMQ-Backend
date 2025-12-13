package services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;

@Service
public class EmailService {
	
	// This Service now only serve as place holder, EmailService is now moved to its own small service app
	// with async event handling using RabbitMQ
	
	private final JavaMailSender mailSender;
	// template dependency here for later purposes
	
	@Value("${spring.mail.username}")
	private String fromEmail;
	
	@Value("${verify.host}")
	private String host;

	public EmailService(JavaMailSender mailSender) {
		super();
		this.mailSender = mailSender;
	}
	
	@Async
	public void sendSimpleMailMessage(String name, String to, String token) {
		
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setSubject("New user account verification");
			message.setFrom(fromEmail);
			message.setTo(to);
			message.setText(EmailUtils.getEmailMessage(name, host, token));
			mailSender.send(message);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}
	
}
