package services;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	@Value("${jwt.secret}")
	private String jwtSecret;
	
	// constructor if needed
	
	
	public SecretKey getKey() {
		byte [] keyBytes = Decoders.BASE64.decode(this.jwtSecret); // decodes the secret in bytes[]
		return Keys.hmacShaKeyFor(keyBytes);
	}
	
	public String generateToken(String useranme) { // username or email
		Map<String, Object> claims = new HashMap<>();
		return Jwts
				.builder()
				.claims()
				.add(claims)
				.subject(useranme)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis()+ 1000 * 60 * 60 * 5)) // five hour
				.and()
				.signWith(getKey())
				.compact();
	
	}
	
	private Claims extractClaims(String token) {
		return Jwts
				.parser()
				.verifyWith(this.getKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
	
	public String extractUsername(String token) {
		return extractClaims(token).getSubject();
	}
	
	private boolean validateExpiry(String token) {
		return extractClaims(token).getExpiration().after(new Date()); // expiration is after the Date now
	}
	
	public boolean validateToken(UserDetails userDetails, String token) {
		String username = extractUsername(token);
		boolean valid = validateExpiry(token);
		
		return (username.equals(userDetails.getUsername()) && valid == true);
	}
	
	
	// and if you didnt have the secret key you can create one using following generateKey method
	public String generateKey() { // but we are importing secret from the props
		try {
			KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
			SecretKey sk = keyGen.generateKey();
			String secret = Base64.getEncoder().encodeToString(sk.getEncoded());
			return secret;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
}
