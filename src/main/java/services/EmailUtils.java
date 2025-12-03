package services;

public class EmailUtils {
	
	public static String getEmailMessage(String name, String host, String token) {
		String text = "Hello " + name + ",\n\nYour new account has been created. Please click the"
				+ " link below to verify you account.\n\n"
				+ getVerificationUrl(host, token) + "\n\nSupport Team";
		
		return text;
	}

	private static String getVerificationUrl(String host, String token) {
		// concatenate the host here
		// replace this with the frontend url
		return host + "/api/auth/public/verify?token=" + token;
	}
}
