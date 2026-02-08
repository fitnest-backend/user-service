package az.fitnest.user.user.adapter.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Configuration for IamServiceClient.
 * Adds the required X-Internal-Service header for service-to-service communication.
 */
public class IamServiceClientConfig {

    @Bean
    public RequestInterceptor internalServiceRequestInterceptor() {
        return template -> {
            
            // 1. Mandatory Internal Header
            template.header("X-Internal-Token", "fitnest-internal-token-2024-secure-v1");
            
            // 2. Clear Authorization to avoid Istio/Envoy 403 for internal calls
            template.removeHeader("Authorization");

            // 3. Forward relevant headers from original request if available
            ServletRequestAttributes requestAttributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            
            if (requestAttributes != null) {
                HttpServletRequest request = requestAttributes.getRequest();
                
                forwardHeader(template, request, "X-User-Id");
                forwardHeader(template, request, "X-User-Email");
                forwardHeader(template, request, "X-User-Roles");
                forwardHeader(template, request, "X-Request-ID");
            }
        };
    }

    private void forwardHeader(RequestTemplate template, HttpServletRequest request, String name) {
        String value = request.getHeader(name);
        if (value != null && !value.isEmpty()) {
            template.header(name, value);
        }
    }
}
