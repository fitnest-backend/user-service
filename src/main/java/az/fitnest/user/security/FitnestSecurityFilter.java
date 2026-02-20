package az.fitnest.user.security;

import az.fitnest.user.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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

@Component
@RequiredArgsConstructor
public class FitnestSecurityFilter extends OncePerRequestFilter {

    private static final java.util.Set<String> SKIP_FILTER_PATH_PREFIXES = java.util.Set.of(
            "/swagger-ui",
            "/v3/api-docs",
            "/actuator",
            "/webjars"
    );

    private final JwtUtil jwtUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return SKIP_FILTER_PATH_PREFIXES.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String gatewayFlag = request.getHeader("X-From-Gateway");
        String userIdHeader = request.getHeader("X-User-Id");
        String requestId = request.getHeader("X-Request-Id");
        String caller = request.getHeader("X-Service-Name");

        // Prefer Pattern A headers if from Gateway
        if ("1".equals(gatewayFlag) && userIdHeader != null && !userIdHeader.isBlank()) {
            authenticateViaPatternA(request, userIdHeader, requestId, caller);
        } else {
            // Legacy/Direct JWT support
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                authenticateViaJwt(authHeader.substring(7));
            }
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateViaPatternA(HttpServletRequest request, String userIdStr, String requestId, String caller) {
        try {
            Long userId = Long.parseLong(userIdStr);
            String scopes = request.getHeader("X-Scopes");

            List<String> roles = new ArrayList<>();
            if (scopes != null && !scopes.isBlank()) {
                roles.addAll(Arrays.stream(scopes.split(" "))
                        .map(String::trim)
                        .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                        .toList());
            } else {
                roles.add("ROLE_USER");
            }

            setAuthentication(userId, "PatternA:" + caller + ":" + requestId, roles);
        } catch (Exception e) {
        }
    }

    private void authenticateViaJwt(String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            List<String> roles = jwtUtil.getRolesFromToken(token);
            setAuthentication(userId, null, roles);
        } catch (Exception e) {
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
