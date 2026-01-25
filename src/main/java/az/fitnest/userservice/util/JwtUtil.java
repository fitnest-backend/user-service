package az.fitnest.userservice.util;

import java.util.function.Function;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {

	private String secretKey="my_secret_key";

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(secretKey.getBytes());
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		Claims claims = Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
		return claimsResolver.apply(claims);
	}

	public Integer getUserIdFromToken(String token) {
		String userIdStr = extractClaim(token, claims -> claims.get("userId", String.class));
		return Integer.valueOf(userIdStr);
	}
}