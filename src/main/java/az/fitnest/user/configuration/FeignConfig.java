package az.fitnest.user.configuration;

import az.fitnest.user.util.UserContext;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public feign.Logger.Level feignLoggerLevel() {
        return feign.Logger.Level.NONE;
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            org.springframework.web.context.request.ServletRequestAttributes attributes =
                (org.springframework.web.context.request.ServletRequestAttributes)
                org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                
                // Forward Pattern A headers
                forwardHeader(request, requestTemplate, "X-User-Id");
                forwardHeader(request, requestTemplate, "X-Tenant-Id");
                forwardHeader(request, requestTemplate, "X-Scopes");
                forwardHeader(request, requestTemplate, "X-Request-Id");
                forwardHeader(request, requestTemplate, "X-From-Gateway");

                // Identify self
                requestTemplate.header("X-Service-Name", "user-service");

                // Legacy support
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && !("storage-worker-service".equals(requestTemplate.feignTarget().name()))) {
                    requestTemplate.header("Authorization", authHeader);
                }
            }
        };
    }

    private void forwardHeader(HttpServletRequest request, feign.RequestTemplate template, String headerName) {
        String val = request.getHeader(headerName);
        if (val != null && !val.isBlank()) {
            template.header(headerName, val);
        }
    }
}
