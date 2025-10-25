package ru.astera.backend.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.astera.backend.entity.User;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class UserDto {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("email")
    private String email;

    @JsonProperty("fullName")
    private String fullName;

    @JsonProperty("role")
    private User.Role role;

    @JsonProperty("isActive")
    private Boolean isActive;

    @JsonProperty("createdAt")
    private OffsetDateTime createdAt;
}
