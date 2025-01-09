package com.slpixe.springshorturl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/a").permitAll() // Allow unauthenticated access to /a
                        .anyRequest().authenticated() // Require authentication for other endpoints
                )
                .formLogin(Customizer.withDefaults()); // Enable default form-based login

        return http.build();
    }
}
