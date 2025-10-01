package ru.astera.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.astera.backend.config.TestContainersConfig;
import ru.astera.backend.dto.LeadRegistrationDto;
import ru.astera.backend.dto.ManagerRegistrationDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Import(TestContainersConfig.class)
@Transactional
class UserRegistrationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerLead_Success() throws Exception {
        LeadRegistrationDto leadDto = new LeadRegistrationDto();
        leadDto.setFullName("Test Client");
        leadDto.setPhone("+7(999)123-45-67");
        leadDto.setEmail("client@test.com");
        leadDto.setOrganization("Test Organization");
        leadDto.setConsentPersonalData(true);

        mockMvc.perform(post("/api/leads/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(leadDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("Test Client"))
                .andExpect(jsonPath("$.email").value("client@test.com"));
    }

    @Test
    @WithMockUser(roles = "admin")
    void registerManager_Success() throws Exception {
        ManagerRegistrationDto managerDto = new ManagerRegistrationDto();
        managerDto.setEmail("manager@test.com");
        managerDto.setFullName("Test Manager");
        managerDto.setPassword("password123");

        mockMvc.perform(post("/api/admin/managers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(managerDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("manager@test.com"))
                .andExpect(jsonPath("$.role").value("manager"));
    }

    @Test
    void registerLead_DuplicateContacts_Conflict() throws Exception {
        LeadRegistrationDto leadDto = new LeadRegistrationDto();
        leadDto.setFullName("Test Client");
        leadDto.setPhone("+7(999)123-45-67");
        leadDto.setEmail("client@test.com");
        leadDto.setOrganization("Test Organization");
        leadDto.setConsentPersonalData(true);

        // First registration
        mockMvc.perform(post("/api/leads/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(leadDto)))
                .andExpect(status().isCreated());

        // Second registration with same contacts
        mockMvc.perform(post("/api/leads/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(leadDto)))
                .andExpect(status().isConflict());
    }
}