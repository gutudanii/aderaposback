package com.adera.aderapos.security.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JwtAuthentication Entry Point
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Commence method to handle unauthorized access attempts.
     *
     * @param request       the HTTP request
     * @param response      the HTTP response
     * @param authException the authentication exception
     * @throws IOException in case of I/O errors
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: " + authException.getMessage());
    }
}
