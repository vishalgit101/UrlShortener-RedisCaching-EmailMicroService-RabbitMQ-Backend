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
	        title = "URL Shortener Microservice with Redis and RabbitMQ",
	        description = """
	            A **production-grade URL Shortener microservice** built with Spring Boot, designed with scalability, security, and performance in mind.

	            ### Key Features
	            - JWT-based authentication and authorization using Spring Security
	            - Email verification and secure password reset via **RabbitMQ (event-driven)**
	            - **Redis** for high-performance caching, rate limiting, and abuse prevention
	            - Stateless, microservice-oriented architecture
	            - QR code generation for shortened URLs
	            - Detailed request logging for security and analytics
	            - Fully documented REST APIs using **Swagger / OpenAPI**

	            ### Demo Account
	            A pre-configured demo user is available for evaluating secured APIs.

	            **Credentials**
	            - Username: `demo@gmail.com`
	            - Password: `demo123`

	            This account allows testing of most protected features while restricting
	            high-risk operations such as role upgrades and user deletion.

	            ### Deployment Notes
	            > ⚠️ This application is hosted on **Render Free Tier**.
	            > SMTP ports are blocked on the free tier, so:
	            > - User registration email verification
	            > - Password reset emails  
	            > will **not work** in this environment.
	            > All other APIs function correctly.
	            """,
	        version = "1.1.0",
	        contact = @Contact(
	            name = "Vishal",
	            email = "vishalgit101@gmail.com",
	            url = "https://github.com/vishalgit101"
	        ),
	        license = @License(
	            name = "MIT License"
	        )
	    ),
	    externalDocs = @ExternalDocumentation(
	        description = "Project source code and additional documentation",
	        url = "https://github.com/vishalgit101/UrlShortener-RedisCaching-EmailMicroService-RabbitMQ-Backend"
	    )
	)


public class LinkShortenerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LinkShortenerApplication.class, args);
	}

}
