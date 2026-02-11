package az.fitnest.user.security;

import az.fitnest.user.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FitnestSecurityFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String internalUserId = request.getHeader("X-User-Id");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authenticateViaJwt(authHeader.substring(7));
        } else if (internalUserId != null && !internalUserId.isBlank()) {
            authenticateViaInternalHeaders(request);
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateViaJwt(String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            List<String> roles = jwtUtil.getRolesFromToken(token);
            setAuthentication(userId, null, roles);
        } catch (Exception e) {
            log.warn("JWT validation failed: {}", e.getMessage());
        }
    }

    private void authenticateViaInternalHeaders(HttpServletRequest request) {
        try {
            Long userId = Long.parseLong(request.getHeader("X-User-Id"));
            String email = request.getHeader("X-User-Email");
            String rolesStr = request.getHeader("X-User-Roles");

            List<String> roles = new ArrayList<>();
            roles.add("ROLE_USER");
            roles.add("ROLE_INTERNAL");

            if (rolesStr != null && !rolesStr.isBlank()) {
                roles.addAll(Arrays.stream(rolesStr.split(","))
                        .map(String::trim)
                        .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                        .toList());
            }

            setAuthentication(userId, email, roles);
        } catch (Exception e) {
            log.warn("Internal header authentication failed: {}", e.getMessage());
        }
    }

    private void setAuthentication(Object principal, String details, List<String> roles) {
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                principal, null, authorities);
        auth.setDetails(details);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
