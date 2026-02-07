package az.fitnest.user.user.adapter.client;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

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
            template.header("X-Internal-Service", "user-service");
            log.debug("Added X-Internal-Service header to iam-service request: {}", template.url());
        };
    }

    @Bean
    feign.Logger.Level feignLoggerLevel() {
        return feign.Logger.Level.FULL;
    }
}
