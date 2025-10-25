package ru.astera.backend.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.astera.backend.entity.User;

@Data
public class UserCreateDto {
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "Full name is required")
    @JsonProperty("fullName")
    private String fullName;

    @NotNull(message = "Role is required")
    @JsonProperty("role")
    private User.Role role;

    @JsonProperty("password")
    private String password;

    @JsonProperty("isActive")
    private Boolean isActive = true;
}
