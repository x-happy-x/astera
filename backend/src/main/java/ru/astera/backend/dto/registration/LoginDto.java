package ru.astera.backend.dto.registration;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDto {

    @NotBlank(message = "Email обязателен для заполнения")
    @Email(message = "Некорректный email")
    private String email;

    @NotBlank(message = "Пароль обязателен для заполнения")
    private String password;

}