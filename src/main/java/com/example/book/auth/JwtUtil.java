package com.example.book.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;

import java.util.Date;

/**
 * Utility class for handling JWT token generation and validation.
 * Provides methods to generate a token (with expiration) and validate an incoming token.
 */
public class JwtUtil {

    /**
     * Secret key used for signing JWT tokens.
     * In production, store this securely (ENV variables or Secrets Manager).
     */
    private static final String SECRET_KEY = "mySecretKey";

    /**
     * Generates a JWT token for a given username.
     *
     * @param username The username for which the token is generated.
     * @return A signed JWT token.
     */
    public static String generateToken(String username) {
        long now = System.currentTimeMillis();
        long expiry = now + 3600000; // 1 hour validity
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(expiry))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    /**
     * Validates a JWT token and extracts the username if valid.
     *
     * @param token The JWT token to validate.
     * @return The username if the token is valid, otherwise {@code null}.
     */
    public static String validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
