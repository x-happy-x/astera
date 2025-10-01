package ru.astera.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.astera.backend.config.SecurityConfig;
import ru.astera.backend.dto.AuthResponseDto;
import ru.astera.backend.dto.LoginDto;
import ru.astera.backend.entity.User;
import ru.astera.backend.exception.InvalidCredentialsException;
import ru.astera.backend.security.JwtAuthenticationFilter;
import ru.astera.backend.service.AuthService;
import ru.astera.backend.service.JwtService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        AuthControllerTest.MockConfig.class
})
class AuthControllerTest {

    @TestConfiguration
    static class MockConfig {
        @Bean
        AuthService authService() {
            return Mockito.mock(AuthService.class);
        }

        @Bean
        JwtService jwtService() {
            return Mockito.mock(JwtService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginDto loginDto;
    private AuthResponseDto authResponse;

    @BeforeEach
    void setUp() {
        loginDto = new LoginDto();
        loginDto.setEmail("admin@example.com");
        loginDto.setPassword("admin");

        authResponse = new AuthResponseDto(
                "test-token",
                "admin@example.com",
                "Administrator",
                User.Role.admin
        );
    }

    @Test
    void login_Success() throws Exception {
        when(authService.login(any(LoginDto.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("test-token"))
                .andExpect(jsonPath("$.email").value("admin@example.com"))
                .andExpect(jsonPath("$.fullName").value("Administrator"))
                .andExpect(jsonPath("$.role").value("admin"));
    }

    @Test
    void login_InvalidCredentials() throws Exception {
        when(authService.login(any(LoginDto.class))).thenThrow(new InvalidCredentialsException("Неверные учетные данные"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Неверные учетные данные"));
    }

    @Test
    void login_ValidationError() throws Exception {
        loginDto.setEmail("");
        loginDto.setPassword("");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ошибки валидации"))
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.password").exists());
    }
}