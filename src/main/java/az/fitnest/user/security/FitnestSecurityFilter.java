package az.fitnest.user.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Fitnest Standard Security Filter.
 *
 * Handles both:
 * 1. Internal service-to-service authentication via X-Internal-Service.
 * 2. External gateway authentication via X-User-Id, X-User-Email, X-User-Roles.
 */
@Slf4j
public class FitnestSecurityFilter extends OncePerRequestFilter {

    private static final String INTERNAL_PATH_PREFIX = "/api/v1/internal";
    private static final String INTERNAL_SERVICE_HEADER = "X-Internal-Service";
    
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_EMAIL_HEADER = "X-User-Email";
    private static final String USER_ROLES_HEADER = "X-User-Roles";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String internalHeader = request.getHeader(INTERNAL_SERVICE_HEADER);

        // 1. Handle Internal Service-to-Service requests
        if (internalHeader != null && !internalHeader.isBlank()) {
            authenticateInternalService(internalHeader);
            filterChain.doFilter(request, response);
            return;
        }

        // Block external access to internal endpoints
        if (path.startsWith(INTERNAL_PATH_PREFIX)) {
            log.warn("Blocked external access to internal endpoint: {} from {}", path, request.getRemoteAddr());
            sendForbidden(response, "Internal endpoints are not accessible externally");
            return;
        }

        // 2. Handle Gateway-authenticated requests
        String userIdStr = request.getHeader(USER_ID_HEADER);
        if (userIdStr != null && !userIdStr.isBlank()) {
            authenticateGatewayUser(request);
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateInternalService(String serviceName) {
        log.debug("Authenticating internal service: {}", serviceName);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_INTERNAL"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "INTERNAL_SERVICE:" + serviceName, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private void authenticateGatewayUser(HttpServletRequest request) {
        String userIdStr = request.getHeader(USER_ID_HEADER);
        String email = request.getHeader(USER_EMAIL_HEADER);
        String rolesStr = request.getHeader(USER_ROLES_HEADER);

        try {
            Long userId = Long.parseLong(userIdStr);
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

            if (rolesStr != null && !rolesStr.isBlank()) {
                authorities.addAll(Arrays.stream(rolesStr.split(","))
                        .map(role -> role.trim().toUpperCase())
                        .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList()));
            }

            // Using userId as principal for type-safe access in UserContext
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userId, null, authorities);
            
            // Store details like email if needed in the authentication object
            auth.setDetails(email);

            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (NumberFormatException e) {
            log.error("Invalid X-User-Id header: {}", userIdStr);
        }
    }

    private void sendForbidden(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"error\":\"%s\"}", message));
    }
}
