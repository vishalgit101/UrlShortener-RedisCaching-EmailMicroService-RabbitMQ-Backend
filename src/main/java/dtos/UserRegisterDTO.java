package dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
	    name = "User Registration Request",
	    description = "Request payload for registering a new user account. Includes user details and credentials required for account creation."
	)
public class UserRegisterDTO {
    @Schema(
            example = "Vishal",
            description = "Full name of the user (2–50 characters)"
        )
	@NotBlank(message = "Full name is required")
    @Size(min = 2, max = 50, message = "Full name must be between 2 and 50 characters")
    private String fullName;

    @Schema(
            example = "vishal@example.com",
            description = "Valid email address used for login and verification"
        )
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;


    @Schema(
        example = "MyPass@123",
        description = "Password (minimum 6 characters)"
    )
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    // Getters and Setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.toLowerCase().trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
