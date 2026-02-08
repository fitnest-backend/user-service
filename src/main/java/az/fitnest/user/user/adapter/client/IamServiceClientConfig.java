package az.fitnest.user.user.adapter.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Configuration for IamServiceClient.
 * Adds the required X-Internal-Service header for service-to-service communication.
 */
@Slf4j
public class IamServiceClientConfig {

    /**
     * Request interceptor that adds X-Internal-Service header for service-to-service calls.
     * This header is required by iam-service to allow access to internal endpoints.
     */
    @Bean
    public RequestInterceptor internalServiceRequestInterceptor() {
        return template -> {
            log.warn(">>> [FEIGN-TRACE] Preparing request: {} {} <<<", template.method(), template.url());
            
            // 1. Mandatory Internal Header
            log.warn(">>> [FEIGN-TRACE] Adding X-Internal-Service: user-service <<<");
            template.header("X-Internal-Service", "user-service");
            
            // 2. Clear Authorization to avoid Istio/Envoy 403 for internal calls
            log.warn(">>> [FEIGN-TRACE] Explicitly removing Authorization header <<<");
            template.removeHeader("Authorization");

            // 3. Forward relevant headers from original request if available
            ServletRequestAttributes requestAttributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            
            if (requestAttributes != null) {
                HttpServletRequest request = requestAttributes.getRequest();
                
                log.warn(">>> [FEIGN-TRACE] Forwarding headers from incoming request <<<");
                forwardHeader(template, request, "X-User-Id");
                forwardHeader(template, request, "X-User-Email");
                forwardHeader(template, request, "X-User-Roles");
                forwardHeader(template, request, "X-Request-ID");
            } else {
                log.warn(">>> [FEIGN-TRACE] No incoming request context available for header forwarding <<<");
            }
        };
    }

    private void forwardHeader(RequestTemplate template, jakarta.servlet.http.HttpServletRequest request, String name) {
        String value = request.getHeader(name);
        if (value != null && !value.isEmpty()) {
            template.header(name, value);
        }
    }

}
