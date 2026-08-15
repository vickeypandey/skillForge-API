package com.vickey.skillforge.config;

import com.vickey.skillforge.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                // REST API using JWT, so CSRF disabled
                .csrf(csrf -> csrf.disable())

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
                                "/api/users/register",
                                "/api/users/login",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/webjars/**"
                        ).permitAll()

                        // Only ADMIN can get all users
                        .requestMatchers("/api/users")
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
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}