package com.elite_gear_backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

import java.util.Arrays;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

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



    private final UserAuthenticationProvider userAuthenticationProvider;

    @SuppressWarnings("removal")
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                .exceptionHandling(customizer -> customizer.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .addFilterBefore(new JwtAuthFilter(userAuthenticationProvider), BasicAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
                )
                .headers(headers -> headers.frameOptions().disable())
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth .requestMatchers(toH2Console()).permitAll())
                .authorizeHttpRequests((requests) -> requests
                        // .requestMatchers("/h2-console/**").permitAll()
                        // .requestMatchers(HttpMethod.GET, "/user/profile").hasAnyRole("ADMIN", "USER")
                        // .requestMatchers(HttpMethod.DELETE, "/cpus/**").hasRole("ADMIN")
                        // .requestMatchers(HttpMethod.POST, "/cpus/**").hasRole("ADMIN")
                        // .requestMatchers(HttpMethod.PUT, "/cpus/**").hasRole("ADMIN")
                        // .requestMatchers(HttpMethod.DELETE, "/rams/**").hasRole("ADMIN")
                        // .requestMatchers(HttpMethod.POST, "/rams/**").hasRole("ADMIN")
                        // .requestMatchers(HttpMethod.PUT, "/rams/**").hasRole("ADMIN")
                        // .requestMatchers(HttpMethod.DELETE, "/coolers/**").hasRole("ADMIN")
                        // .requestMatchers(HttpMethod.POST, "/coolers/**").hasRole("ADMIN")
                        // .requestMatchers(HttpMethod.PUT, "/coolers/**").hasRole("ADMIN")    
                        .anyRequest().permitAll())
        ;
        return http.build();
    }
}