package az.fitnest.user.shared.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtUtil {

	@Value("${jwt.secret:}")
	private String secretKey;

	@Value("${jwt.issuer:fitnest}")
	private String issuer;

	private SecretKey signingKey;
	private JwtParser jwtParser;

	@PostConstruct
	void init() {
		if (secretKey == null || secretKey.isBlank()) {
			throw new IllegalStateException("jwt.secret must be configured (do not rely on defaults).");
		}
		byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
		if (keyBytes.length < 32) {
			throw new IllegalStateException("jwt.secret is too short for HS256 (need at least 32 bytes / 256 bits).");
		}

		this.signingKey = Keys.hmacShaKeyFor(keyBytes);
		this.jwtParser = Jwts.parserBuilder()
				.setSigningKey(signingKey)
				.requireIssuer(issuer)
				.build();
	}

	public Long getUserIdFromToken(String token) {
		if (token == null || token.isBlank()) {
			throw new JwtException("JWT token is missing.");
		}
		Claims claims = parseClaims(token);
		String sub = claims.getSubject();
		if (sub == null || sub.isBlank()) {
			throw new JwtException("JWT subject (sub) is missing.");
		}
		try {
			return Long.parseLong(sub);
		} catch (NumberFormatException ex) {
			throw new JwtException("JWT subject (sub) is not a valid user id.", ex);
		}
	}

	private Claims parseClaims(String token) {
		try {
			Jws<Claims> jws = jwtParser.parseClaimsJws(token);
			return jws.getBody();
		} catch (JwtException e) {
			throw e;
		} catch (Exception e) {
			throw new JwtException("JWT parsing failed.", e);
		}
	}
}
