package com.Ayan.Mondal.VOTEONN.CONFIG;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    public static final String[] PUBLIC_API = {
            "/api/register**",
            "/api/login",
            "/api/all/user/**",
            "/api/voter/add",
            "/api/party",
            "/api/voter/verify",
            "/api/voter/verify-otp",
            "/api/voter/submit-vote",
            "/api/voters/register",
            "/api/voters/validate",
            "/api/voters/register-with-face",
            "/api/voters/validate",
            "/api/userface/register",
            "/api/userface/validate",
            "/api/voters/verify-otp",
            "/api/voters/verify",
            "/error",
            "/favicon.ico"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_API).permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}