package az.fitnest.user.configuration;

import az.fitnest.user.util.UserContext;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
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
            String userId = String.valueOf(UserContext.getCurrentUserId());
            String email = UserContext.getCurrentUserEmail();

            if (userId != null && !userId.equals("null")) {
                requestTemplate.header("X-User-Id", userId);
            }
            if (email != null) {
                requestTemplate.header("X-User-Email", email);
            }
            
            // Forward Authorization header if present in current request context
            // (Standard Feign interceptor pattern)
            org.springframework.web.context.request.ServletRequestAttributes attributes = 
                (org.springframework.web.context.request.ServletRequestAttributes) 
                org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
            
            if (attributes != null) {
                // Skip sending Authorization header to terabox-worker-service as it doesn't need user tokens
                // and forwarding them might cause 403 errors if the token is rejected downstream
                if (requestTemplate.feignTarget() != null && "terabox-worker-service".equals(requestTemplate.feignTarget().name())) {
                    return;
                }

                String authHeader = attributes.getRequest().getHeader("Authorization");
                if (authHeader != null) {
                    requestTemplate.header("Authorization", authHeader);
                }
            }
        };
    }
}
