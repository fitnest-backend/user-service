package az.fitnest.userservice.util;

import org.springframework.stereotype.Component;


import az.fitnest.userservice.exception.CustomException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Component
public class UserContextUtil {

    public static Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof Integer userId) {
            return userId;
        }

        throw new CustomException("User not found", "Authentication is invalid or user not found", "UserNotFound", 401,
				null);
    }
}




