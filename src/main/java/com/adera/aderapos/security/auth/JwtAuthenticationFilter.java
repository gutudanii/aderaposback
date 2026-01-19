package com.adera.aderapos.security.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * JwtAuthentication Filter to validate JWT tokens in incoming requests.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    /**
     * Filter method to validate JWT token from the Authorization header.
     *
     * @param request     the HTTP request
     * @param response    the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException in case of servlet errors
     * @throws IOException      in case of I/O errors
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (tokenProvider.validateToken(token)) {
                UUID userId = tokenProvider.getUserId(token);
                UUID shopId = tokenProvider.getShopId(token);
                String role = tokenProvider.getRole(token);

                UserDetails userDetails = org.springframework.security.core.userdetails.User
                        .withUsername(userId.toString())
                        .password("") // Password not needed here
                        .roles(role)
                        .build();

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
