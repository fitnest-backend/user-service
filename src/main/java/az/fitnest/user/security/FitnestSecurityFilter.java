package az.fitnest.user.security;

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

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String userIdStr = request.getHeader("X-User-Id");

        if (userIdStr != null && !userIdStr.isBlank()) {
            try {
                Long userId = Long.parseLong(userIdStr);
                String scopes = request.getHeader("X-Scopes");
                String rolesHeader = request.getHeader("X-User-Roles");

                List<String> roles = new ArrayList<>();
                String rolesToParse = (scopes != null && !scopes.isBlank()) ? scopes : rolesHeader;

                if (rolesToParse != null && !rolesToParse.isBlank()) {
                    roles.addAll(Arrays.stream(rolesToParse.split("[,\\s]+"))
                            .map(String::trim)
                            .filter(r -> !r.isEmpty())
                            .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                            .toList());
                } else {
                    roles.add("ROLE_USER");
                }

                authenticate(userId, roles, request);
                filterChain.doFilter(request, response);
                return;
            } catch (NumberFormatException ignored) {
            }
        }

        if (request.getRequestURI().startsWith("/api/v1/internal")) {
            authenticateInternalService(request);
        }

        filterChain.doFilter(request, response);
    }

    private void authenticate(Object principal, List<String> roles, HttpServletRequest request) {
        List<String> finalRoles = new ArrayList<>(roles);
        if (request.getRequestURI().startsWith("/api/v1/internal") && 
            !finalRoles.contains("ROLE_INTERNAL")) {
            finalRoles.add("ROLE_INTERNAL");
        }

        List<SimpleGrantedAuthority> authorities = finalRoles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                principal, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private void authenticateInternalService(HttpServletRequest request) {
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_INTERNAL"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "INTERNAL_SERVICE", null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
