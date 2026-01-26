package az.fitnest.userservice.shared.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import az.fitnest.userservice.shared.exception.ResourceNotFoundException;

@Component
public class UserContextUtil {

	public static Long getCurrentUserId() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth != null && auth.getPrincipal() instanceof Long userId) {
			return userId;
		}

		if (auth != null && auth.getPrincipal() instanceof Integer userId) {
			return userId.longValue();
		}

		throw new ResourceNotFoundException("User not authenticated");
	}

}
