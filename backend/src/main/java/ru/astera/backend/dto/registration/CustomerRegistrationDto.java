package ru.astera.backend.dto.registration;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerRegistrationDto {

    @NotBlank(message = "ФИО обязательно для заполнения")
    private String fullName;

    @NotBlank(message = "Телефон обязателен для заполнения")
    private String phone;

    @NotBlank(message = "Email обязателен для заполнения")
    @Email(message = "Некорректный email")
    private String email;

    @NotBlank(message = "Название организации обязательно для заполнения")
    private String organization;

    @NotBlank(message = "Пароль обязателен для заполнения")
    @Size(min = 6, message = "Пароль должен содержать минимум 6 символов")
    @Size(max = 32, message = "Пароль не должен содержать более 32 символов")
    private String password;

}