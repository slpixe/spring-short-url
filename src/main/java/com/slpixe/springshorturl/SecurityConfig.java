package com.slpixe.springshorturl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    /**
     * Make sure JwtAuthenticationFilter is declared as a bean.
     * This ensures Spring can auto-inject it into securityFilterChain(...)
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil, UserRepo userRepo) {
        return new JwtAuthenticationFilter(jwtUtil, userRepo);
    }

    /**
     * The main security filter chain:
     *  - Disables CSRF
     *  - Permits select endpoints (register, login, etc.)
     *  - Permits /error to avoid 403 on error forward
     *  - Enforces JWT-based stateless sessions
     *  - Installs JwtAuthenticationFilter before UsernamePasswordAuthenticationFilter
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/register", "/api/login", "/s/**", "/api/setup-otp",
                                "/health", "/a", "/b/aaa").permitAll()
                        .requestMatchers("/error").permitAll() // explicitly allow the Spring Boot error page
                        .requestMatchers("/api/urls/**").authenticated()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Our custom JWT filter should run before the default UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Optional: Only needed if you do username/password auth
     * and call authenticationManager.authenticate(...) somewhere
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
