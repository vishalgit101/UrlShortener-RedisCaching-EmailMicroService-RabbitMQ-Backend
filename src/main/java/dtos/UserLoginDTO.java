package dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
	    name = "UserLoginRequest",
	    description = "DTO used for user login"
	)
public class UserLoginDTO {
    @Schema(
            description = "Registered email address of the user",
            example = "user@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
	private String email;
    
    @Schema(
            description = "User account password",
            example = "MyPassword@123",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
	private String password;
    
	//private boolean verified;
	
	
	/*public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}*/

	public UserLoginDTO() {
		// TODO Auto-generated constructor stub
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
	
	
}
