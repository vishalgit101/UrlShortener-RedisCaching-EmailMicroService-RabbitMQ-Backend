package services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import configs.RabbitMQConfig;
import model.MailMessage;

@Service
public class MailMessageProducerService {
	private static final Logger logger = LoggerFactory.getLogger(MailMessageProducerService.class);
	
	private final RabbitTemplate rabbitTemplate;

	public MailMessageProducerService(RabbitTemplate rabbitTemplate) {
		super();
		this.rabbitTemplate = rabbitTemplate;
	}
	
	// later move the exception to the global exception handling
	public String sendVerificationEmail(String name, String to, String token) {
		MailMessage message = new MailMessage();
		
		message.setId(UUID.randomUUID().toString());
		message.setCreatedAt(LocalDateTime.now());
		message.setEmail(to);
		message.setFullname(name);
		message.setPriority("HIGH");
		message.setToken(token);
		
		try {
			logger.info("Sending verification email with ID {} to RabbitMQ", message.getId());
			rabbitTemplate.convertAndSend(RabbitMQConfig.APP_EXCHANGE, RabbitMQConfig.EMAIL_VERIFICATION_ROUTING_KEY, message);
			logger.info("Verification Email sent to the Queue");
			return message.getId();
		} catch (Exception e) {
			logger.error("Failed to send the email: {}", e.getMessage());
			throw new RuntimeException("Failed to send the verification email");
		}
	}
	
	// later move the exception to the global exception handling
	public void sendPasswordResetRequest(String email, String token) {
		MailMessage message = new MailMessage();
		
		message.setId(UUID.randomUUID().toString());
		message.setEmail(email);
		message.setToken(token);
		message.setPriority("HIGH");
		message.setCreatedAt(LocalDateTime.now());
		
		try {
			logger.info("Meessage Producer sending Password Reset Request to the Queue");
			this.rabbitTemplate.convertAndSend(RabbitMQConfig.APP_EXCHANGE, RabbitMQConfig.EMAIL_PASSWORD_RESET_ROUTING_KEY, message);
			logger.info("Password reset req sent to the Queue ");
		} catch (Exception e) {
			throw new RuntimeException("Failed to send the request for password reset");
		}
	}
	
}
