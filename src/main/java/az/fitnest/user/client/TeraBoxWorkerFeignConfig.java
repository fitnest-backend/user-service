package az.fitnest.user.client;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class TeraBoxWorkerFeignConfig {

    @Bean
    public RequestInterceptor teraBoxRequestInterceptor() {
        return requestTemplate -> {
            requestTemplate.removeHeader("Authorization");
            requestTemplate.header("X-Internal-Token", "shared-secret-token");
        };
    }
}
