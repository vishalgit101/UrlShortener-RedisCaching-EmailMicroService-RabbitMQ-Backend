package configs;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


@Configuration
@EnableRabbit
public class RabbitMQConfig {
	//Exchange
	public static final String APP_EXCHANGE = "url.app.exchange";
	
	// Routing Key
	public static final String EMAIL_VERIFICATION_ROUTING_KEY = "url.email.verification";
	
	public static final String EMAIL_PASSWORD_RESET_ROUTING_KEY = "url.email.password.reset";
	
	// Queues, technically not required on the producer side but still added it withou bindings as place holder if later binding is required for some reasons
	public static final String EMAIL_VERIFICATION_QUEUE = "url.email.verification.queue";
	
	public static final String EMAIL_PASSWORD_RESET_QUEUE = "url.email.password.reset.queue";
	
	@Bean
	public TopicExchange appExchange() {
		return new TopicExchange(APP_EXCHANGE, true, false);
	}
	
	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(messageConverter());
		return rabbitTemplate;
	}
	
	@Bean
	public Jackson2JsonMessageConverter messageConverter() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		return new Jackson2JsonMessageConverter(mapper);
	}
	
}
