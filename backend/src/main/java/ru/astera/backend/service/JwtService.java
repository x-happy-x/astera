package ru.astera.backend.service;

import java.util.Date;

public interface JwtService {
    String generateToken(String email, String role);

    String extractEmail(String token);

    String extractRole(String token);

    Date extractExpiration(String token);

    <T> T extractClaim(String token, java.util.function.Function<io.jsonwebtoken.Claims, T> claimsResolver);

    Boolean isTokenExpired(String token);

    Boolean validateToken(String token, String email);
}