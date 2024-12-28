package com.elite_gear_backend.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserAuthenticationProvider userAuthenticationProvider;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.OPTIONS.name()));
        configuration.setAllowedHeaders(Arrays.asList(
                HttpHeaders.AUTHORIZATION,
                HttpHeaders.CONTENT_TYPE));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                .exceptionHandling(customizer -> 
                    customizer.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .addFilterBefore(new JwtAuthFilter(userAuthenticationProvider), BasicAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/cart/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/coolers/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/coolers/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/cpu/**").hasRole("ADMIN")//
                        .requestMatchers(HttpMethod.PUT, "/cpu/**").hasRole("ADMIN")//
                        .requestMatchers(HttpMethod.GET, "/motherboard/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/motherboard/**").hasRole("ADMIN")//
                        .requestMatchers(HttpMethod.PUT, "/motherboard/**").hasRole("ADMIN")//
                        .requestMatchers(HttpMethod.POST, "/ram/**").hasRole("ADMIN")//
                        .requestMatchers(HttpMethod.PUT, "/ram/**").hasRole("ADMIN")//
                        .requestMatchers("/orders/**").authenticated()
                        .requestMatchers("/products/{productId}/photos").hasRole("ADMIN")//
                        .anyRequest().permitAll()
                )
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
