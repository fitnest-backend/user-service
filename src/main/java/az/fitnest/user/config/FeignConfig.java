package az.fitnest.user.config;

import az.fitnest.user.shared.util.UserContext;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

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
        };
    }
}
