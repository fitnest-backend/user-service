package az.fitnest.user.user.adapter.client;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

/**
 * Configuration for MediaClient.
 * Adds the required X-Internal-Service header for service-to-service communication.
 */
@Slf4j
public class MediaClientConfig {

    /**
     * Request interceptor that adds X-Internal-Service header for service-to-service calls.
     * This header is required by media-service to allow access to internal endpoints.
     */
    @Bean
    public RequestInterceptor internalMediaRequestInterceptor() {
        return template -> {
            log.trace(">>> [FEIGN-TRACE] Preparing Media request: {} {} <<<", template.method(), template.url());
            
            // 1. Mandatory Internal Header
            template.header("X-Internal-Token", "fitnest-internal-token-2024-secure-v1");
            
            // 2. Clear Authorization to avoid Istio/Envoy 403 for internal calls
            template.removeHeader("Authorization");

            log.trace(">>> [FEIGN-TRACE] Media Headers: {} <<<", template.headers());
        };
    }
}
