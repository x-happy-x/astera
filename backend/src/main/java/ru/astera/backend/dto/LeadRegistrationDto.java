package ru.astera.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LeadRegistrationDto {

    @NotBlank(message = "ФИО обязательно для заполнения")
    private String fullName;

    @NotBlank(message = "Телефон обязателен для заполнения")
    private String phone;

    @NotBlank(message = "Email обязателен для заполнения")
    @Email(message = "Некорректный email")
    private String email;

    @NotBlank(message = "Название организации обязательно для заполнения")
    private String organization;

    @NotNull(message = "Согласие на обработку персональных данных обязательно")
    private Boolean consentPersonalData;
}