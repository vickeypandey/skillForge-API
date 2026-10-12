package com.vickey.skillforge.config;

import com.vickey.skillforge.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.List;


@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    @Value("${app.cors.allowed-origins:http://localhost:5500,http://127.0.0.1:5500}")
    private String allowedOrigins;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // REST API using JWT, so CSRF disabled
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .logout(logout -> logout.disable())

                // No HTTP session; JWT comes with every protected request
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // Handle authentication and authorization failures
                .exceptionHandling(exception ->
                        exception

                                // User is NOT authenticated
                                // No JWT / invalid JWT
                                .authenticationEntryPoint(
                                        (request, response, authException) -> {

                                            response.setStatus(
                                                    HttpServletResponse.SC_UNAUTHORIZED
                                            );
                                        }
                                )

                                // User IS authenticated,
                                // but does not have required permission/role
                                .accessDeniedHandler(
                                        (request, response, accessDeniedException) -> {

                                            response.setStatus(
                                                    HttpServletResponse.SC_FORBIDDEN
                                            );
                                        }
                                )
                )

                // Authorization rules
                .authorizeHttpRequests(auth -> auth

                        // Public APIs
                        .requestMatchers(
                                "/api/v1/auth/register",
                                "/api/v1/auth/login",
                                "/api/v1/auth/refresh",
                                "/api/v1/auth/logout",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/webjars/**",
                                "/actuator/health",
                                "/actuator/health/**"
                        ).permitAll()

                        // Skill discovery is public; managing your own skills still requires authentication.
                        .requestMatchers(HttpMethod.GET, "/api/v1/skills/me").authenticated()
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/skills",
                                "/api/v1/skills/*",
                                "/api/v1/skills/search",
                                "/api/v1/skills/category/**",
                                "/api/v1/skills/level/**")
                        .permitAll()

                        // Only ADMIN can get all users
                        .requestMatchers("/api/v1/users")
                        .hasRole("ADMIN")

                        // Everything else only needs valid authentication
                        .anyRequest()
                        .authenticated()
                )

                // Run JWT filter before Spring's username/password filter
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toList();
        configuration.setAllowedOrigins(origins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}
