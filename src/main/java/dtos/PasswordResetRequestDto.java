package dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
	    name = "PasswordResetRequest",
	    description = "DTO used to reset user password. The token should be copied from the password reset email."
	)
public class PasswordResetRequestDto {
	

    @Schema(
        description = "Token sent to the user's email for password reset. Copy it from the email.",
        example = "abc123xyz",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
	private String token;
    
    @Schema(
            description = "New password chosen by the user",
            example = "NewPassword@123",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
	private String newPassword;
    
    @Schema(
            description = "Confirm the new password",
            example = "NewPassword@123",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
	private String confirmPassword;
	
	public PasswordResetRequestDto(String token, String newPassword, String confirmPassword) {
		super();
		this.token = token;
		this.newPassword = newPassword;
		this.confirmPassword = confirmPassword;
	}
	
	public PasswordResetRequestDto() {
		// TODO Auto-generated constructor stub
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	
	
	
}
