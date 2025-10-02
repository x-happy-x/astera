package ru.astera.backend.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerCreateDto {
    @JsonProperty("email")
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;

    @JsonProperty("fullName")
    @NotBlank(message = "ФИО не может быть пустым")
    private String fullName;

    @JsonProperty("phone")
    @NotBlank(message = "Телефон не может быть пустым")
    private String phone;

    @JsonProperty("organization")
    @NotBlank(message = "Организация не может быть пустой")
    private String organization;

    @JsonProperty("password")
    private String password;
}