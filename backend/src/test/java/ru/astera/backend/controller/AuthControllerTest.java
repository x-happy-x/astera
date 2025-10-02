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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.astera.backend.config.SecurityConfig;
import ru.astera.backend.dto.registration.*;
import ru.astera.backend.entity.User;
import ru.astera.backend.exception.InvalidCredentialsException;
import ru.astera.backend.security.JwtAuthenticationFilter;
import ru.astera.backend.service.AuthService;
import ru.astera.backend.service.JwtService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
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
                UUID.randomUUID(),
                "test-token",
                "admin@example.com",
                "Administrator",
                User.Role.admin
        );
        Mockito.clearInvocations(authService, jwtService);
    }

    /* =======================
       /api/auth/login
       ======================= */

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
        when(authService.login(any(LoginDto.class)))
                .thenThrow(new InvalidCredentialsException("Неверные учетные данные"));

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

    /* =======================
       /api/auth/customer/register
       ======================= */

    @Test
    void customerRegister_Success() throws Exception {
        var req = new ClientRegistrationDto();
        req.setEmail("cust@example.com");
        req.setFullName("Клиент");
        req.setOrganization("ООО Ромашка");
        req.setPhone("+7 900 111-22-33");
        req.setPassword("password123");

        var resp = new CustomerResponseDto();
        resp.setId(UUID.randomUUID());
        resp.setToken("cust-token");
        resp.setEmail("cust@example.com");
        resp.setFullName("Клиент");
        resp.setRole(User.Role.customer);
        resp.setPhone("+7 900 111-22-33");
        resp.setOrganization("ООО Ромашка");

        when(authService.registerCustomer(any(ClientRegistrationDto.class))).thenReturn(resp);

        mockMvc.perform(post("/api/auth/customer/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("cust-token"))
                .andExpect(jsonPath("$.email").value("cust@example.com"))
                .andExpect(jsonPath("$.role").value("customer"))
                .andExpect(jsonPath("$.phone").value("+7 900 111-22-33"))
                .andExpect(jsonPath("$.organization").value("ООО Ромашка"));
    }

    @Test
    void customerRegister_ValidationError() throws Exception {
        var req = new ClientRegistrationDto();
        req.setEmail("");          // invalid
        req.setFullName("");       // invalid
        req.setOrganization("");   // invalid
        req.setPhone("");          // invalid
        req.setPassword("123");    // too short

        mockMvc.perform(post("/api/auth/customer/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ошибки валидации"))
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.fullName").exists())
                .andExpect(jsonPath("$.errors.organization").exists())
                .andExpect(jsonPath("$.errors.phone").exists())
                .andExpect(jsonPath("$.errors.password").exists());
    }

    /* =======================
       /api/auth/customer/login
       ======================= */

    @Test
    void customerLogin_Success() throws Exception {
        var req = new LoginDto();
        req.setEmail("cust@example.com");
        req.setPassword("pass");

        var resp = new CustomerResponseDto();
        resp.setId(UUID.randomUUID());
        resp.setToken("ctok");
        resp.setEmail("cust@example.com");
        resp.setFullName("Клиент");
        resp.setRole(User.Role.customer);
        resp.setPhone("+7 900 222-33-44");
        resp.setOrganization("ООО Василёк");

        when(authService.loginCustomer(any(LoginDto.class))).thenReturn(resp);

        mockMvc.perform(post("/api/auth/customer/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("ctok"))
                .andExpect(jsonPath("$.email").value("cust@example.com"))
                .andExpect(jsonPath("$.role").value("customer"))
                .andExpect(jsonPath("$.phone").value("+7 900 222-33-44"))
                .andExpect(jsonPath("$.organization").value("ООО Василёк"));
    }

    @Test
    void customerLogin_InvalidCredentials() throws Exception {
        var req = new LoginDto();
        req.setEmail("cust@example.com");
        req.setPassword("bad");

        when(authService.loginCustomer(any(LoginDto.class)))
                .thenThrow(new InvalidCredentialsException("Неверные учетные данные"));

        mockMvc.perform(post("/api/auth/customer/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Неверные учетные данные"));
    }

    /* =======================
       /api/auth/manager/register
       ======================= */

    @Test
    @WithMockUser(roles = "admin")
    void managerRegister_Success_AsAdmin() throws Exception {
        var req = new ManagerRegistrationDto();
        req.setEmail("m@example.com");
        req.setFullName("Manager");
        req.setPassword("Secure123");

        var resp = new AuthResponseDto(
                UUID.randomUUID(),
                "mtok",
                "m@example.com",
                "Manager",
                User.Role.manager
        );

        when(authService.register(any(ManagerRegistrationDto.class))).thenReturn(resp);

        mockMvc.perform(post("/api/auth/manager/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mtok"))
                .andExpect(jsonPath("$.email").value("m@example.com"))
                .andExpect(jsonPath("$.role").value("manager"));
    }

    @Test
    void managerRegister_Forbidden_WithoutRole() throws Exception {
        var req = new ManagerRegistrationDto();
        req.setEmail("m@example.com");
        req.setFullName("Manager");
        req.setPassword("Secure123");

        mockMvc.perform(post("/api/auth/manager/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(authService);
    }

    @Test
    @WithMockUser(roles = "admin")
    void managerRegister_ValidationError() throws Exception {
        var req = new ManagerRegistrationDto();
        req.setEmail("");
        req.setFullName("");
        req.setPassword("");

        mockMvc.perform(post("/api/auth/manager/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ошибки валидации"))
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.fullName").exists())
                .andExpect(jsonPath("$.errors.password").exists());
    }
}