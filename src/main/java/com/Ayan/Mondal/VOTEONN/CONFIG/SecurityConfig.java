package com.Ayan.Mondal.VOTEONN.CONFIG;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import com.Ayan.Mondal.VOTEONN.CONFIG.JwtRequestFilter;
import com.Ayan.Mondal.VOTEONN.CONFIG.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        @Autowired
        private JwtRequestFilter jwtRequestFilter;

        @Autowired
        private UserDetailsServiceImpl userDetailsService;

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }

        @Bean
        public DaoAuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
                authProvider.setUserDetailsService(userDetailsService);
                authProvider.setPasswordEncoder(passwordEncoder());
                return authProvider;
        }

        public static final String[] PUBLIC_API = {
                // Paths with /api prefix (for local testing)
                "/api/register",
                "/api/login",
                "/api/auth/google",
                "/api/auth/refresh",
                "/api/auth/logout",
                "/api/captcha",
                "/api/voters/register",
                "/api/voters/register-with-face",
                "/api/voters/verify-credentials",
                "/api/forgot-password**",
                "/api/forgot-password/**",
                "/api/party/**",
                "/api/party**",

                // Paths WITHOUT /api prefix (forwarded by Netlify proxy)
                "/register",
                "/login",
                "/auth/google",
                "/auth/refresh",
                "/auth/logout",
                "/captcha",
                "/voters/register",
                "/voters/register-with-face",
                "/voters/verify-credentials",
                "/forgot-password**",
                "/forgot-password/**",
                "/party/**",
                "/party**",

                // General assets
                "/error",
                "/api/correction/submit",
                "/api/deletion/submit",
                "/correction/submit",
                "/deletion/submit",
                "/uploads/**",
                "/*.html",
                "/*.js",
                "/*.css",
                "/partySelection/**",
        };
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

                http
                                .csrf(csrf -> csrf.disable())
                                .cors(Customizer.withDefaults())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(PUBLIC_API).permitAll()
                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                                // Admin-only endpoints
                                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                                .requestMatchers("/api/correction/pending").hasRole("ADMIN")
                                                .requestMatchers("/api/deletion/pending").hasRole("ADMIN")
                                                .requestMatchers("/api/correction/*/resolve").hasRole("ADMIN")
                                                .requestMatchers("/api/deletion/*/resolve").hasRole("ADMIN")
                                                .anyRequest().authenticated())
                                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

                http.authenticationProvider(authenticationProvider());
                http.userDetailsService(userDetailsService);

                http.addFilterBefore(jwtRequestFilter,
                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {

                CorsConfiguration configuration = new CorsConfiguration();

                configuration.setAllowedOriginPatterns(List.of(
                                "http://127.0.0.1:*",
                                "http://localhost:*",
                                "https://votonn.netlify.app",
                                "https://*.netlify.app"));

                configuration.setAllowedMethods(List.of("*"));
                configuration.setAllowedHeaders(List.of("*"));
                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration("/**", configuration);

                return source;
        }
}