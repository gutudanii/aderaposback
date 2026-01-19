package com.adera.aderapos.security.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.adera.aderapos.security.auth.JwtTokenProvider;

import java.util.UUID;

/**
 * Utility class for security-related operations.
 */
@Component
public class SecurityUtils {
    private static JwtTokenProvider jwtTokenProvider;

    @Autowired
    public SecurityUtils(JwtTokenProvider jwtTokenProvider) {
        SecurityUtils.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Retrieves the current authenticated user's ID.
     *
     * @return the user ID of the currently authenticated user, or null if not authenticated
     */
    public static String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }

    /**
     * Checks if the current authenticated user has a specific role.
     *
     * @param role the role to check (without the "ROLE_" prefix)
     * @return true if the user has the specified role, false otherwise
     */
    public static boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

    /**
     * Retrieves the current authenticated user's shop ID as UUID.
     *
     * @return the shop ID of the currently authenticated user, or null if not available
     */
    public static UUID getCurrentShopId() {
        ServletRequestAttributes attrs = (ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return null;
        }
        HttpServletRequest request = attrs.getRequest();
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            UUID shopId = jwtTokenProvider.getShopId(token);
            return shopId != null ? UUID.fromString(shopId.toString()) : null;
        }
        return null;
    }
}
