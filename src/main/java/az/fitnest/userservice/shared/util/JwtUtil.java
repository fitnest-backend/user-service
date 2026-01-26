package az.fitnest.userservice.shared.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {

	@Value("${jwt.secret:my_secret_key_that_is_at_least_32_characters_long}")
	private String secretKey;
	
	@Value("${jwt.issuer:fitnest}")
	private String issuer;

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(secretKey.getBytes());
	}

	public Long getUserIdFromToken(String token) {
		Claims claims = parseClaims(token);
		String sub = claims.getSubject();
		if (sub == null || sub.isBlank()) {
			throw new IllegalArgumentException("JWT subject (sub) is missing.");
		}
		try {
			return Long.parseLong(sub);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("JWT subject (sub) is not a valid Long.");
		}
	}

	private Claims parseClaims(String token) {
		Jws<Claims> jws = Jwts.parser()
				.verifyWith(getSigningKey())
				.requireIssuer(issuer)
				.build()
				.parseSignedClaims(token);

		return jws.getPayload();
	}
}
