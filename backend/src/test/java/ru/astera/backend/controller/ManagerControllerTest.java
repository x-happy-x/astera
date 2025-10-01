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
import ru.astera.backend.dto.ManagerRegistrationDto;
import ru.astera.backend.entity.User;
import ru.astera.backend.exception.UserAlreadyExistsException;
import ru.astera.backend.security.JwtAuthenticationFilter;
import ru.astera.backend.service.JwtService;
import ru.astera.backend.service.UserService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ManagerController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        ManagerControllerTest.MockConfig.class
})
class ManagerControllerTest {
    @TestConfiguration
    static class MockConfig {
        @Bean
        UserService userService() {
            return Mockito.mock(UserService.class);
        }

        @Bean
        JwtService jwtService() {
            return Mockito.mock(JwtService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private ManagerRegistrationDto managerDto;
    private User manager;

    @BeforeEach
    void setUp() {
        managerDto = new ManagerRegistrationDto();
        managerDto.setEmail("manager@test.com");
        managerDto.setFullName("Test Manager");
        managerDto.setPassword("password123");

        manager = new User();
        manager.setId(UUID.randomUUID());
        manager.setEmail("manager@test.com");
        manager.setFullName("Test Manager");
        manager.setRole(User.Role.manager);
        manager.setIsActive(true);
    }

    @Test
    @WithMockUser(roles = "admin")
    void registerManager_Success() throws Exception {
        when(userService.createManager(any(ManagerRegistrationDto.class))).thenReturn(manager);

        mockMvc.perform(post("/api/admin/managers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(managerDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("manager@test.com"))
                .andExpect(jsonPath("$.fullName").value("Test Manager"))
                .andExpect(jsonPath("$.role").value("manager"))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    @WithMockUser(roles = "admin")
    void registerManager_UserAlreadyExists() throws Exception {
        when(userService.createManager(any(ManagerRegistrationDto.class)))
                .thenThrow(new UserAlreadyExistsException("Пользователь с таким email уже существует"));

        mockMvc.perform(post("/api/admin/managers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(managerDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Пользователь с таким email уже существует"));
    }

    @Test
    @WithMockUser(roles = "manager")
    void registerManager_AccessDenied() throws Exception {
        mockMvc.perform(post("/api/admin/managers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(managerDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void registerManager_Unauthorized() throws Exception {
        mockMvc.perform(post("/api/admin/managers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(managerDto)))
                .andExpect(status().isUnauthorized());
    }
}