package az.fitnest.user.config;

import az.fitnest.user.shared.util.UserContext;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public feign.Logger.Level feignLoggerLevel() {
        return feign.Logger.Level.FULL;
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
                String authHeader = attributes.getRequest().getHeader("Authorization");
                if (authHeader != null) {
                    requestTemplate.header("Authorization", authHeader);
                }
            }
        };
    }
}
