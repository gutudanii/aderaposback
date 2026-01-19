package com.adera.aderapos.security.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

/**
 * Utility class for generating and validating JWT tokens.
 */
@Component
public class JwtTokenProvider {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long expirationMs = 900_000; // 15 min

    /**
     * Generate JWT token
     *
     * @param userId the user ID
     * @param shopId the shop ID
     * @param role   the user role
     * @return the generated JWT token
     */
    public String generateToken(UUID userId, UUID shopId, String role) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("shopId", shopId)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    /**
     * Validate JWT token
     *
     * @param token the JWT token
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Extract userId
     *
     * @param token the JWT token
     * @return the user ID
     */
    public UUID getUserId(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
        return UUID.fromString(claims.getSubject());
    }

    /**
     * Extract shopId
     *
     * @param token the JWT token
     * @return the shop ID
     */
    public UUID getShopId(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
        return claims.get("shopId", UUID.class);
    }

    /**
     * Extract role
     *
     * @param token the JWT token
     * @return the user role
     */
    public String getRole(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
        return claims.get("role", String.class);
    }
}
