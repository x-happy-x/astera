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
import ru.astera.backend.dto.LeadRegistrationDto;
import ru.astera.backend.entity.Lead;
import ru.astera.backend.exception.LeadAlreadyExistsException;
import ru.astera.backend.security.JwtAuthenticationFilter;
import ru.astera.backend.service.JwtService;
import ru.astera.backend.service.LeadService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LeadController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        LeadControllerTest.MockConfig.class
})
class LeadControllerTest {

    @TestConfiguration
    static class MockConfig {
        @Bean
        LeadService leadService() {
            return Mockito.mock(LeadService.class);
        }

        @Bean
        JwtService jwtService() {
            return Mockito.mock(JwtService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LeadService leadService;

    @Autowired
    private JwtService jwtService;

    private LeadRegistrationDto leadDto;
    private Lead lead;

    @BeforeEach
    void setUp() {
        leadDto = new LeadRegistrationDto();
        leadDto.setFullName("Test Client");
        leadDto.setPhone("+7(999)123-45-67");
        leadDto.setEmail("client@test.com");
        leadDto.setOrganization("Test Organization");
        leadDto.setConsentPersonalData(true);

        lead = new Lead();
        lead.setId(UUID.randomUUID());
        lead.setFullName("Test Client");
        lead.setPhone("+7(999)123-45-67");
        lead.setEmail("client@test.com");
        lead.setOrganization("Test Organization");
        lead.setConsentPersonalData(true);
    }

    @Test
    void registerLead_Success() throws Exception {
        when(leadService.createLead(any(LeadRegistrationDto.class))).thenReturn(lead);

        mockMvc.perform(post("/api/leads/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(leadDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fullName").value("Test Client"))
                .andExpect(jsonPath("$.phone").value("+7(999)123-45-67"))
                .andExpect(jsonPath("$.email").value("client@test.com"))
                .andExpect(jsonPath("$.organization").value("Test Organization"))
                .andExpect(jsonPath("$.consentPersonalData").value(true));
    }

    @Test
    void registerLead_AlreadyExists() throws Exception {
        when(leadService.createLead(any(LeadRegistrationDto.class)))
                .thenThrow(new LeadAlreadyExistsException("Клиент с такими контактными данными уже зарегистрирован"));

        mockMvc.perform(post("/api/leads/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(leadDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Клиент с такими контактными данными уже зарегистрирован"));
    }

    @Test
    void registerLead_ValidationError() throws Exception {
        leadDto.setFullName("");
        leadDto.setEmail("invalid-email");
        leadDto.setConsentPersonalData(null);

        mockMvc.perform(post("/api/leads/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(leadDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ошибки валидации"))
                .andExpect(jsonPath("$.errors.fullName").exists())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.consentPersonalData").exists());
    }
}
