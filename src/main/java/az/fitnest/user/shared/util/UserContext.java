package az.fitnest.user.shared.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Optional;

/**
 * Type-safe utility for accessing current user context from Spring Security.
 */
@Slf4j
public class UserContext {

    /**
     * Get the current user ID.
     * @return Long userId or null if not authenticated
     */
    public static Long getCurrentUserId() {
        return getPrincipalAsLong().orElse(null);
    }

    /**
     * Get the current user ID or throw exception.
     * @throws RuntimeException if not authenticated
     */
    public static Long getRequiredUserId() {
        return getPrincipalAsLong()
                .orElseThrow(() -> new RuntimeException("User not authenticated"));
    }

    /**
     * Get current user email from details.
     */
    public static String getCurrentUserEmail() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(auth -> (String) auth.getDetails())
                .orElse(null);
    }

    /**
     * Check if current principal is an internal service.
     */
    public static boolean isInternalService() {
        return hasRole("ROLE_INTERNAL");
    }

    /**
     * Check if user has a specific role.
     */
    public static boolean hasRole(String role) {
        String roleName = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        
        return auth.getAuthorities().contains(new SimpleGrantedAuthority(roleName));
    }

    private static Optional<Long> getPrincipalAsLong() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            return Optional.empty();
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof Long) {
            return Optional.of((Long) principal);
        }
        
        // Handle cases where principal might be stored as String (legacy or internal)
        if (principal instanceof String) {
            try {
                return Optional.of(Long.parseLong((String) principal));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }

        return Optional.empty();
    }
}
