package az.fitnest.user.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Feign client configuration for forwarding authentication headers
 * to downstream services during service-to-service communication.
 */
@Configuration
public class FeignConfig {

    /**
     * Request interceptor that forwards the Authorization header (JWT token)
     * and other relevant headers from the original request to Feign client calls.
     * This ensures that service-to-service communication maintains the authentication context.
     */
    @Bean
    public RequestInterceptor authorizationForwardingInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                ServletRequestAttributes requestAttributes = 
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                
                if (requestAttributes != null) {
                    HttpServletRequest request = requestAttributes.getRequest();
                    
                    // Forward Authorization header (JWT token)
                    String authHeader = request.getHeader("Authorization");
                    if (authHeader != null && !authHeader.isEmpty()) {
                        template.header("Authorization", authHeader);
                    }
                    
                    // Forward X-User-Id header for internal service communication
                    String userId = request.getHeader("X-User-Id");
                    if (userId != null && !userId.isEmpty()) {
                        template.header("X-User-Id", userId);
                    }
                    
                    // Forward X-User-Email header
                    String userEmail = request.getHeader("X-User-Email");
                    if (userEmail != null && !userEmail.isEmpty()) {
                        template.header("X-User-Email", userEmail);
                    }
                    
                    // Forward X-User-Roles header
                    String userRoles = request.getHeader("X-User-Roles");
                    if (userRoles != null && !userRoles.isEmpty()) {
                        template.header("X-User-Roles", userRoles);
                    }
                    
                    // Forward X-Request-ID for tracing
                    String requestId = request.getHeader("X-Request-ID");
                    if (requestId != null && !requestId.isEmpty()) {
                        template.header("X-Request-ID", requestId);
                    }
                }
            }
        };
    }
}
