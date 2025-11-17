package com.society.management.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret.access}")
    private String accessTokenSecret;

    @Value("${jwt.secret.refresh}")
    private String refreshTokenSecret;

    @Value("${jwt.expiration.access:3600000}")
    private long accessTokenExpiration;

    @Value("${jwt.expiration.refresh:86400000}")
    private long refreshTokenExpiration;

    // ✅ Generate SecretKey
    private SecretKey getAccessSigningKey() {
        return Keys.hmacShaKeyFor(accessTokenSecret.getBytes(StandardCharsets.UTF_8));
    }

    private SecretKey getRefreshSigningKey() {
        return Keys.hmacShaKeyFor(refreshTokenSecret.getBytes(StandardCharsets.UTF_8));
    }

    // Generate Access Token
    public String generateAccessToken(String userId, String email, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .setSubject(userId)
                .claim("email", email)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getAccessSigningKey())  // ✅ FIXED: No SignatureAlgorithm needed in 0.12.x
                .compact();
    }

    // Generate Refresh Token
    public String generateRefreshToken(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpiration);

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getRefreshSigningKey())  // ✅ FIXED: No SignatureAlgorithm needed
                .compact();
    }

    // Validate Access Token
    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(getAccessSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    // Validate Refresh Token
    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(getRefreshSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("Expired refresh token: {}", e.getMessage());
        } catch (JwtException e) {
            log.error("Invalid refresh token: {}", e.getMessage());
        }
        return false;
    }

    // Extract User ID from Token
    public String extractUserId(String token) {
        return Jwts.parser()
                .setSigningKey(getAccessSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Extract User ID from Refresh Token
    public String extractUserIdFromRefreshToken(String token) {
        return Jwts.parser()
                .setSigningKey(getRefreshSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Extract Email from Token
    public String extractEmail(String token) {
        return Jwts.parser()
                .setSigningKey(getAccessSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("email", String.class);
    }

    // Extract Role from Token
    public String extractRole(String token) {

        return Jwts.parser()
                .setSigningKey(getAccessSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    // Extract Username (email) from token for UserDetailsService
    public String extractUsername(String token) {
        return extractEmail(token);
    }

    // Check if Token is Expired
    public boolean isTokenExpired(String token) {
        try {

            Date expiration = Jwts.parser()
                    .setSigningKey(getAccessSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    // Validate token with UserDetails (for Spring Security)
    public Boolean isTokenValid(String token, org.springframework.security.core.userdetails.UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
