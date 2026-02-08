package az.fitnest.user.user.adapter.client;

import feign.RequestInterceptor;

import org.springframework.context.annotation.Bean;

/**
 * Configuration for MediaClient.
 * Adds the required X-Internal-Service header for service-to-service communication.
 */
public class MediaClientConfig {

    /**
     * Request interceptor that adds X-Internal-Service header for service-to-service calls.
     * This header is required by media-service to allow access to internal endpoints.
     */
    @Bean
    public RequestInterceptor internalMediaRequestInterceptor() {
        return template -> {
            
            // 1. Mandatory Internal Header
            template.header("X-Internal-Token", "fitnest-internal-token-2024-secure-v1");
            
            // 2. Clear Authorization to avoid Istio/Envoy 403 for internal calls
            template.removeHeader("Authorization");
        };
    }
}
