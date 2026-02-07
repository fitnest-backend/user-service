package az.fitnest.user.config;

import az.fitnest.user.security.gateway.GatewayHeaderAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Security configuration for user-service.
 * 
 * Uses a single SecurityFilterChain with proper request matchers
 * to handle both internal (service-to-service) and external requests.
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Public endpoints that don't require authentication.
     * Internal endpoints are called by other services (iam-service, etc.)
     */
    private static final String[] PUBLIC_ENDPOINTS = {
            // Internal service-to-service endpoints - MUST be first and most specific
            "/api/v1/internal/**",
            
            // Swagger/OpenAPI
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            
            // Actuator endpoints
            "/actuator/**",
            "/health/**",
            
            // Public file access
            "/api/v1/files/profiles/**",
            
            // Reference data endpoints
            "/api/v1/reference/**",
            
            // Error handling
            "/error"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configuring user-service security filter chain");
        
        http
            // Disable CSRF for stateless REST API
            .csrf(AbstractHttpConfigurer::disable)
            
            // Configure CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Stateless session management
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                // Allow all public endpoints without authentication
                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                
                // Allow OPTIONS requests for CORS preflight
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            
            // Add custom authentication filter for gateway headers
            .addFilterBefore(
                new GatewayHeaderAuthenticationFilter(), 
                UsernamePasswordAuthenticationFilter.class
            );
        
        log.info("User-service security configured. Public endpoints: {}", 
                 Arrays.toString(PUBLIC_ENDPOINTS));
        
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"
        ));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of(
            "Authorization", "X-User-Id", "X-User-Email", "X-User-Roles", "X-Request-ID"
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}