package az.fitnest.userservice.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import az.fitnest.userservice.exception.ResourceNotFoundException;

@Component
public class UserContextUtil {

	public static Integer getCurrentUserId() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth != null && auth.getPrincipal() instanceof Integer userId) {
			return userId;
		}

		throw new ResourceNotFoundException("User not authenticated");
	}

}
