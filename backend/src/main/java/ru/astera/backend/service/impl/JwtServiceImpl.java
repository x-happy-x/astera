package ru.astera.backend.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.astera.backend.service.JwtService;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${app.jwt.secret:change-this-to-32B-or-more}")
    private String secret;

    @Value("${app.jwt.expiration:86400000}")
    private Long expiration;

    private Key getSigningKey() {
        // ВАЖНО: для HS256 нужен ключ >= 256 бит (32 байта).
        // Если secret храните в base64, раскомментируйте следующее:
        // byte[] keyBytes = Decoders.BASE64.decode(secret);
        // return Keys.hmacShaKeyFor(keyBytes);
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateToken(UUID userId, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("uid", userId.toString());
        return createToken(claims, email);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public String extractRole(String token) {
        return extractClaim(token, c -> (String) c.get("role"));
    }

    @Override
    public UUID extractUserId(String token) {
        String uid = extractClaim(token, c -> (String) c.get("uid"));
        return uid == null ? null : UUID.fromString(uid);
    }

    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    @Override
    public Boolean validateToken(String token, String email) {
        String extractedEmail = extractEmail(token);
        return extractedEmail != null && extractedEmail.equals(email) && !isTokenExpired(token);
    }
}