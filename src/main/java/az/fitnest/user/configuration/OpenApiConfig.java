package az.fitnest.user.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${springdoc.server-url:}")
    private String serverUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("User Backend API")
                        .version("1.0.0")
                        .description("Fitnest User Service endpoints")
                        .contact(new Contact()
                                .name("FitNest Team")
                                .email("support@fitnest.az")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter your JWT token")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));

        if (serverUrl != null && !serverUrl.isEmpty()) {
            openAPI.servers(List.of(new Server().url(serverUrl).description("API Server")));
        }

        return openAPI;
    }
}
