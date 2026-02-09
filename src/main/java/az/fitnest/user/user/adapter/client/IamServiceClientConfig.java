package az.fitnest.user.user.adapter.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Configuration for IamServiceClient.
 * Adds the required X-Internal-Service header for service-to-service communication.
 */
@Configuration
public class IamServiceClientConfig {


    @Bean
    public RequestInterceptor internalServiceRequestInterceptor() {
        return template -> {
            // 1. Forward relevant headers from SecurityContext and Request

            // 2. Forward headers from current request and SecurityContext
            
            // Get User ID from Security Context (JWT)
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (principal instanceof Long) {
                    String userId = String.valueOf(principal);
                    template.header("X-User-Id", userId);
                    System.out.println("DEBUG: Injected X-User-Id from SecurityContext: " + userId);
                } else if (principal instanceof String && !"anonymousUser".equals(principal)) {
                     // Fallback for String principal
                    template.header("X-User-Id", (String) principal);
                    System.out.println("DEBUG: Injected X-User-Id from SecurityContext (String): " + principal);
                }
            }

            // Forward other headers from request if available
            ServletRequestAttributes requestAttributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            
            if (requestAttributes != null) {
                HttpServletRequest request = requestAttributes.getRequest();
                forwardHeader(template, request, "Authorization");
                forwardHeader(template, request, "X-User-Email");
                forwardHeader(template, request, "X-User-Roles");
                forwardHeader(template, request, "X-Request-ID");
            }
        };
    }

    @Bean
    feign.Logger.Level feignLoggerLevel() {
        return feign.Logger.Level.FULL;
    }

    private void forwardHeader(RequestTemplate template, HttpServletRequest request, String name) {
        String value = request.getHeader(name);
        if (value != null && !value.isEmpty()) {
            template.header(name, value);
            System.out.println("DEBUG: Forwarding header " + name + ": " + value);
        }
    }
}
