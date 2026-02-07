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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Security configuration for user-service.
 * 
 * - Internal endpoints (/api/v1/internal/**): Permit all (service-to-service)
 * - External endpoints: Require authentication via gateway headers (X-User-Id)
 * - Public endpoints: Swagger, actuator, files
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info(">>> USER-SERVICE SECURITY CONFIG LOADED - VERSION 2026-02-07-v4 <<<");
        
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                // 1. INTERNAL SERVICE-TO-SERVICE - No auth required
                .requestMatchers(new AntPathRequestMatcher("/api/v1/internal/**")).permitAll()
                
                // 2. PUBLIC ENDPOINTS - Swagger, actuator, public files
                .requestMatchers(new AntPathRequestMatcher("/swagger-ui.html")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/v3/api-docs/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/actuator/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/health/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/v1/files/profiles/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/v1/reference/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/error")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/**", HttpMethod.OPTIONS.name())).permitAll()
                
                // 3. ALL OTHER ENDPOINTS - Require authentication (via gateway headers)
                .anyRequest().authenticated()
            )
            // Gateway header authentication filter for external requests
            .addFilterBefore(
                new GatewayHeaderAuthenticationFilter(), 
                UsernamePasswordAuthenticationFilter.class
            );
        
        log.info("User-service security configured: internal=permitAll, external=authenticated");
        
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