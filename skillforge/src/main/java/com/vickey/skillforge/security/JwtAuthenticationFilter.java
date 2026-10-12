package com.vickey.skillforge.security;

import com.vickey.skillforge.entity.User;
import com.vickey.skillforge.repository.UserRepository;
import com.vickey.skillforge.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import io.jsonwebtoken.JwtException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository) {

        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Get Authorization header
        String authHeader = request.getHeader("Authorization");

        // 2. If JWT is not present, continue request
        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        // 3. Remove "Bearer " and get only JWT
        String token = authHeader.substring(7).trim();
        if (token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        final String email;
        try {
            email = jwtService.extractEmail(token);
        } catch (JwtException | IllegalArgumentException ex) {
            // Leave the request unauthenticated. Protected routes are rejected
            // by Spring Security's authentication entry point.
            filterChain.doFilter(request, response);
            return;
        }

        // 5. Check that user is not already authenticated
        if (email != null &&
                SecurityContextHolder
                        .getContext()
                        .getAuthentication() == null) {

            // 6. Find actual user from database
            User user = userRepository
                    .findByEmail(email)
                    .orElse(null);

            // 7. Validate JWT
            if (user != null &&
                    jwtService.isTokenValid(
                            token,
                            user.getEmail())) {

                // 8. Create Spring Authentication object
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                user.getEmail(),
                                null,
                                Collections.singletonList(
                                        new SimpleGrantedAuthority(
                                                "ROLE_" + user.getRole().name()
                                        )
                                )
                        );

                // 9. Tell Spring Security user is authenticated
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            }
        }

        // 10. Continue request
        filterChain.doFilter(request, response);
    }
}
