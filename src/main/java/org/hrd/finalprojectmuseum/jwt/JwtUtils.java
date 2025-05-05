package org.hrd.finalprojectmuseum.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Component
public class JwtUtils {
    private final long expiration = TimeUnit.MINUTES.toMillis(120);

    //generate reset token
    public String generateResetToken(String email) {
        long expiration = TimeUnit.MINUTES.toMillis(2);
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(generateSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // generate token for user
    public String generateToken(String email, UUID userId, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("roles", List.of(role)) // List of roles
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(generateSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    // generate secret key for signing token
    private SecretKey generateSignKey() {
        String secret = "aWxvdmV5b3VpaGF0ZXlvdXRoaXNpc215c2VjcmV0ZmluZG15aWZ5b3VjYW5paG9wZXlvdWRvaXRiZXR0ZXI=";
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    // retrieving any information from token (Need the secret key)
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(generateSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // extract a specific claim from the JWT token’s claims (user's info)
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // retrieve email from jwt token
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", String.class));
    }

    // retrieve role from jwt token
    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", List.class));
    }

    // retrieve expiration date from jwt token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    // check expired token
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }


    // check if token is valid
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(generateSignKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.out.println("JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("JWT claims string is empty: " + e.getMessage());
        }

        return false;
    }
}

