package az.fitnest.user.client;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

public class TeraBoxWorkerFeignConfig {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TeraBoxWorkerFeignConfig.class);

    @Bean
    public RequestInterceptor teraBoxRequestInterceptor() {
        return requestTemplate -> {
            logger.debug("TeraBoxWorkerFeignConfig: Injecting X-Internal-Token header for URL: {}", requestTemplate.url());
            requestTemplate.removeHeader("Authorization");
            requestTemplate.header("X-Internal-Token", "shared-secret-token");
        };
    }

    @Bean
    feign.Logger.Level feignLoggerLevel() {
        return feign.Logger.Level.FULL;
    }
}
