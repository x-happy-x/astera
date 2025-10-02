package ru.astera.backend.dto.registration;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ManagerRegistrationDto {

    @NotBlank(message = "Email обязателен для заполнения")
    @Email(message = "Некорректный email")
    private String email;

    @NotBlank(message = "ФИО обязательно для заполнения")
    private String fullName;

    @NotBlank(message = "Пароль обязателен для заполнения")
    @Size(min = 6, message = "Пароль должен содержать минимум 6 символов")
    private String password;

}