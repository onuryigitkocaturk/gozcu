package com.onuryigitkocaturk.query_monitor.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

// JwtUtil, jwt tokenler'ı üreten ve doğrulayan sınıf.
// @Component = "ben bir bean'im".
@Component
public class JwtUtil {

    // application.prop'tan basit değerler inject ediliyor.
    // jwt secret, üretilen token'ı imzalamak için kullanılıyor.
    // hmac = hash-based message authentication code.

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration-ms}") long expirationMs) {
        // token'ı imzalayan keyimiz bu
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes()); // jwt -> key nesnesi dönüşümü
                                                                // Keys, jjwt kütüphanesinin sunduğu
                                                                // yardımcı bir sınıf
        this.expirationMs = expirationMs;
    }

    public String generateToken(String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, String username) {
        Claims claims = parseClaims(token);
        return claims.getSubject().equals(username) && claims.getExpiration().after(new Date());
    }
    // token'ı 3 parçaya ayırır, secret key kullanarak header+payload'ı tekrar hesaplar
    // doğruysa içeriği açar, değilse exception fırlatır.
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
