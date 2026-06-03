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

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import com.Ayan.Mondal.VOTEONN.CONFIG.JwtRequestFilter;
import com.Ayan.Mondal.VOTEONN.CONFIG.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
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
                        "/api/register**",
                        "/api/login",
                        "/api/auth/google",
                        "/api/captcha",
                        "/api/voters/register",
                        "/api/voters/register-with-face",
                        "/error",
                        "/api/forgot-password**",
                        "/api/forgot-password/**", // Fixed: added /
                        "/api/correction/pending", // TEMPORARY
                        "/api/deletion/pending", // TEMPORARY
                        "/api/party/**",
                        "/api/party**",
                        "/api/voters/verify",
                        "/api/voters/verify-otp",
                        "/api/voter/submit-vote",
                        "/api/correction/submit",
                        "/api/deletion/submit",
        };

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

                http
                                .csrf(csrf -> csrf.disable())
                                .cors(Customizer.withDefaults())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(PUBLIC_API).permitAll()
                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
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

                configuration.setAllowedOrigins(List.of(
                                "http://127.0.0.1:5500",
                                "http://localhost:5500",
                                "http://127.0.0.1:5503",
                                "http://localhost:5503"));

                configuration.setAllowedMethods(List.of("*"));
                configuration.setAllowedHeaders(List.of("*"));
                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration("/**", configuration);

                return source;
        }
}