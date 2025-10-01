package ru.astera.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.astera.backend.entity.User;

@Data
@AllArgsConstructor
public class AuthResponseDto {

    private String token;
    private String email;
    private String fullName;
    private User.Role role;
}