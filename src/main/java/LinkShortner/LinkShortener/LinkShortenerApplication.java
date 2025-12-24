package LinkShortner.LinkShortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@SpringBootApplication
@ComponentScan(basePackages = {"controllers", "services", "repos", "filters", "exceptions", "configs", "runner"})
@EntityScan(basePackages = {"entities"})
@EnableJpaRepositories(basePackages = {"repos"})
@OpenAPIDefinition(
		info = @Info(
				title = "Url Shortner Microservice Project with Redis and RabbitMQ",
				description = "A production-grade URL Shortener microservice built with Spring Boot, designed with scalability, security, and performance in mind.\n"
						+ "\n"
						+ "The system implements a complete user authentication and authorization flow using Spring Security and JWT, including email verification and secure password reset via an asynchronous, event-driven email queue powered by RabbitMQ.\n"
						+ "\n"
						+ "Redis is used for high-performance caching, rate limiting, and abuse prevention to efficiently handle high-traffic scenarios. The application follows a stateless, microservice-oriented architecture with clean separation of concerns.\n"
						+ "\n"
						+ "Additional features include QR code generation for shortened URLs, detailed request logging for security and analytics, and fully documented REST APIs using Swagger/OpenAPI for easy testing and integration.\n"
						+ "\n"
						+ "This project demonstrates real-world backend engineering practices such as asynchronous processing, secure authentication, distributed caching, and scalable system design.\n"
						+ "",
				version = "1.0.0",
				contact = @Contact(
						name = "Vishal",
						email = "vishalgit101@gmail.com",
						url = "Placeholder for Portfolio site"
						),
				license = @License(
						name = "MIT License"
						)
				),
		externalDocs = @ExternalDocumentation(
			    description = "Additional Information: A pre-configured demo user with limited administrative privileges is available for evaluating secured APIs. This account allows testing of most protected features while restricting high-risk operations such as role upgrades and user deletion. Demo credentials are documented in the project repository README.",
			    url = "Repo PLaceholder"
				)
		)
public class LinkShortenerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LinkShortenerApplication.class, args);
	}

}
