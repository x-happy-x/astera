package ru.astera.backend.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class CustomerDto {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("email")
    private String email;

    @JsonProperty("fullName")
    private String fullName;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("organization")
    private String organization;

    @JsonProperty("isActive")
    private Boolean isActive;

    @JsonProperty("createdAt")
    private OffsetDateTime createdAt;
}