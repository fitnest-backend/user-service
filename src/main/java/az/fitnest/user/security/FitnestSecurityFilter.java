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
 * Standard security filter for Fitnest microservices.
 * Handles Internal Service-to-Service (ROLE_INTERNAL) and Gateway User (ROLE_USER) authentication.
 */
@Slf4j
public class FitnestSecurityFilter extends OncePerRequestFilter {

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
        String userIdStr = request.getHeader(USER_ID_HEADER);

        // 1. Internal Service-to-Service Authentication
        if (internalHeader != null && !internalHeader.isBlank()) {
            log.trace("Internal service authentication: {} calling {}", internalHeader, path);
            authenticateInternalService(internalHeader);
        }
        // 2. Gateway User Authentication
        else if (userIdStr != null && !userIdStr.isBlank()) {
            log.trace("Gateway user authentication: ID {} calling {}", userIdStr, path);
            authenticateGatewayUser(request);
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateInternalService(String serviceName) {
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

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userId, null, authorities);
            
            auth.setDetails(email);
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (NumberFormatException e) {
            log.warn("Invalid X-User-Id format: {}", userIdStr);
        }
    }
}
