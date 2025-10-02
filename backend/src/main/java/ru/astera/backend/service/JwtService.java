package ru.astera.backend.service;

import io.jsonwebtoken.Claims;

import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

public interface JwtService {
    String generateToken(UUID userId, String email, String role);

    String extractEmail(String token);

    String extractRole(String token);

    UUID extractUserId(String token);          // <--

    Date extractExpiration(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    Boolean isTokenExpired(String token);

    Boolean validateToken(String token, String email);
}
