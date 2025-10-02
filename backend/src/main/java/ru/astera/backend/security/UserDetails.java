package ru.astera.backend.security;

import java.util.UUID;

public record UserDetails(UUID userId, String email) {
}
