package com.ashish.authandsessionmanagment.configs;

import com.ashish.authandsessionmanagment.filters.JwtAuthenticationFilter;
import com.ashish.authandsessionmanagment.handlers.OAuth2Handler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;



import java.util.List;


@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2Handler oAuth2Handler;



    private static final String[] publicPaths = {
            "/auth/**",              // Allow all authentication-related paths (including refresh)
            "/error",                // Allow error handling without authentication
            "/v3/api-docs/**",       // Swagger UI docs should be publicly available
            "/swagger-ui/**",        // Swagger UI assets
            "/swagger-ui.html",      // Swagger UI HTML page
            "/home.html",            // Public home page
            "/oauth2/**",            // OAuth2 login routes
            "/auth/refresh"          // Allow refresh token endpoint without authentication
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfiguration()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(publicPaths).permitAll() // Allow public paths without authentication
                        .anyRequest().authenticated()            // All other requests require authentication
                )
                .csrf(AbstractHttpConfigurer::disable)         // Disable CSRF protection for REST APIs
                .formLogin(AbstractHttpConfigurer::disable)   // Disable form-based login
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless session management
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // Add JWT authentication filter
                .oauth2Login(oAuthLogin -> oAuthLogin
                        .failureUrl("/login?error=true")
                        .successHandler(oAuth2Handler)
                );
        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfiguration() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // Add cache for CORS preflight requests

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}