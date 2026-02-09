package az.fitnest.user.user.adapter.client;

import feign.RequestInterceptor;

import org.springframework.context.annotation.Bean;

/**
 * Configuration for MediaClient.
 * Adds the required X-Internal-Service header for service-to-service communication.
 */
@org.springframework.context.annotation.Configuration
public class MediaClientConfig {

    /**
     * Request interceptor that removes Authorization header for service-to-service calls.
     * This is used for internal calls where mutual TLS or other Istio mechanisms provide security.
     */
    @Bean
    public RequestInterceptor internalMediaRequestInterceptor() {
        return template -> {
            // 1. Clear Authorization to avoid Istio/Envoy 403 for internal calls
            template.removeHeader("Authorization");
        };
    }
}
