package az.fitnest.user.profile.adapter.client;

import az.fitnest.user.config.FeignConfig;
import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(FeignConfig.class)
public class IdentityServiceClientConfig {


}
