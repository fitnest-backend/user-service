package az.fitnest.user.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import az.fitnest.user.shared.util.JwtUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class FitnestSecurityFilter extends OncePerRequestFilter {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_EMAIL_HEADER = "X-User-Email";
    private static final String USER_ROLES_HEADER = "X-User-Roles";

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Validate JWT directly (public or internal delegation)
            authenticateViaJwt(authHeader.substring(7));
        } else if (request.getRequestURI().startsWith("/api/v1/internal")) {
            // Internal call without JWT - Rely on mesh for access control
            authenticateViaInternalHeaders(request);
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateViaInternalHeaders(HttpServletRequest request) {
        String userIdStr = request.getHeader(USER_ID_HEADER);
        
        if (userIdStr != null && !userIdStr.isBlank()) {
            log.debug("Authenticating internal request via headers for user: {}", userIdStr);
            authenticateGatewayUser(request);
        } else {
            log.debug("Internal service-to-service call detected on {}", request.getRequestURI());
            authenticateInternalService();
        }
    }

    private void authenticateViaJwt(String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
            
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userId, null, authorities);
            
            SecurityContextHolder.getContext().setAuthentication(auth);
            log.debug("Authenticated user {} via JWT", userId);
        } catch (Exception e) {
            log.warn("JWT validation failed: {}", e.getMessage());
        }
    }

    private void authenticateInternalService() {
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_INTERNAL"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "INTERNAL_SERVICE", null, authorities);
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
            // Keep ROLE_INTERNAL if code still checks it
            authorities.add(new SimpleGrantedAuthority("ROLE_INTERNAL"));

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
            log.debug("Authenticated internal request for user {} with ROLE_INTERNAL", userId);
        } catch (NumberFormatException e) {
            // Ignore invalid user ID format
        }
    }
}
