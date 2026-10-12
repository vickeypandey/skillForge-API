package com.vickey.skillforge;

import com.vickey.skillforge.repository.UserRepository;
import com.vickey.skillforge.security.JwtAuthenticationFilter;
import com.vickey.skillforge.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class JwtAuthenticationFilterTest {
    @Test
    void malformedBearerTokenDoesNotCrashOrAuthenticateRequest() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
                new JwtService("01234567890123456789012345678901", 60_000),
                mock(UserRepository.class));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer not-a-jwt");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean continued = new AtomicBoolean();
        SecurityContextHolder.clearContext();

        filter.doFilter(request, response, (req, res) -> continued.set(true));

        assertTrue(continued.get());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
