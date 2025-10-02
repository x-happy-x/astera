package ru.astera.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.astera.backend.config.TestContainersConfig;
import ru.astera.backend.dto.admin.CustomerCreateDto;
import ru.astera.backend.dto.admin.CustomerUpdateDto;
import ru.astera.backend.entity.CustomerProfile;
import ru.astera.backend.entity.User;
import ru.astera.backend.repository.CustomerProfileRepository;
import ru.astera.backend.repository.UserRepository;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestContainersConfig.class)
@Transactional
@WithMockUser(roles = {"admin", "manager"})
class AdminControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private User testUser1;
    private User testUser2;
    private CustomerProfile testCustomer1;
    private CustomerProfile testCustomer2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Создаем тестовые данные
        testUser1 = new User();
        testUser1.setEmail("customer1@test.com");
        testUser1.setFullName("Тестовый Клиент 1");
        testUser1.setRole(User.Role.customer);
        testUser1.setIsActive(true);
        testUser1 = userRepository.save(testUser1);

        testCustomer1 = new CustomerProfile();
        testCustomer1.setUser(testUser1);
        testCustomer1.setPhone("+7 900 111-22-33");
        testCustomer1.setOrganization("ООО Тест 1");
        testCustomer1 = customerProfileRepository.save(testCustomer1);

        testUser2 = new User();
        testUser2.setEmail("customer2@test.com");
        testUser2.setFullName("Тестовый Клиент 2");
        testUser2.setRole(User.Role.customer);
        testUser2.setIsActive(false);
        testUser2 = userRepository.save(testUser2);

        testCustomer2 = new CustomerProfile();
        testCustomer2.setUser(testUser2);
        testCustomer2.setPhone("+7 900 222-33-44");
        testCustomer2.setOrganization("ООО Тест 2");
        testCustomer2 = customerProfileRepository.save(testCustomer2);
    }

    /* =======================
       GET /api/admin/customers
       ======================= */

    @Test
    void getCustomers_Success_WithDefaultPagination() throws Exception {
        mockMvc.perform(get("/api/admin/customers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.customers", hasSize(2)))
                .andExpect(jsonPath("$.totalCustomers").value(2))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.pageSize").value(20))
                .andExpect(jsonPath("$.customers[0].email", anyOf(equalTo("customer1@test.com"), equalTo("customer2@test.com"))))
                .andExpect(jsonPath("$.customers[0].fullName", anyOf(equalTo("Тестовый Клиент 1"), equalTo("Тестовый Клиент 2"))));
    }

    @Test
    void getCustomers_Success_WithCustomPagination() throws Exception {
        mockMvc.perform(get("/api/admin/customers")
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customers", hasSize(1)))
                .andExpect(jsonPath("$.totalCustomers").value(2))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.pageSize").value(1));
    }

    @Test
    @WithAnonymousUser
    void getCustomers_Forbidden_WithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/admin/customers"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "customer")
    void getCustomers_Forbidden_AsCustomer() throws Exception {
        mockMvc.perform(get("/api/admin/customers"))
                .andExpect(status().isForbidden());
    }

    /* =======================
       GET /api/admin/customers/{id}
       ======================= */

    @Test
    void getCustomer_Success() throws Exception {
        mockMvc.perform(get("/api/admin/customers/{id}", testUser1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser1.getId().toString()))
                .andExpect(jsonPath("$.email").value("customer1@test.com"))
                .andExpect(jsonPath("$.fullName").value("Тестовый Клиент 1"))
                .andExpect(jsonPath("$.phone").value("+7 900 111-22-33"))
                .andExpect(jsonPath("$.organization").value("ООО Тест 1"))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void getCustomer_NotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        mockMvc.perform(get("/api/admin/customers/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    /* =======================
       POST /api/admin/customers
       ======================= */

    @Test
    void createCustomer_Success() throws Exception {
        CustomerCreateDto dto = new CustomerCreateDto();
        dto.setEmail("newcustomer@test.com");
        dto.setFullName("Новый Клиент");
        dto.setPhone("+7 900 333-44-55");
        dto.setOrganization("ООО Новая");
        dto.setPassword("password123");

        mockMvc.perform(post("/api/admin/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("newcustomer@test.com"))
                .andExpect(jsonPath("$.fullName").value("Новый Клиент"))
                .andExpect(jsonPath("$.phone").value("+7 900 333-44-55"))
                .andExpect(jsonPath("$.organization").value("ООО Новая"))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void createCustomer_Conflict_EmailExists() throws Exception {
        CustomerCreateDto dto = new CustomerCreateDto();
        dto.setEmail("customer1@test.com");
        dto.setFullName("Дубликат");
        dto.setPhone("+7 900 555-66-77");
        dto.setOrganization("ООО Дубликат");

        mockMvc.perform(post("/api/admin/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    void createCustomer_ValidationError() throws Exception {
        CustomerCreateDto dto = new CustomerCreateDto();
        dto.setEmail("invalid-email");
        dto.setFullName("");
        dto.setPhone("");
        dto.setOrganization("");

        mockMvc.perform(post("/api/admin/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    /* =======================
       PUT /api/admin/customers/{id}
       ======================= */

    @Test
    void updateCustomer_Success() throws Exception {
        CustomerUpdateDto dto = new CustomerUpdateDto();
        dto.setEmail("updated@test.com");
        dto.setFullName("Обновленный Клиент");
        dto.setPhone("+7 900 777-88-99");
        dto.setOrganization("ООО Обновленная");
        dto.setIsActive(false);

        mockMvc.perform(put("/api/admin/customers/{id}", testUser1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@test.com"))
                .andExpect(jsonPath("$.fullName").value("Обновленный Клиент"))
                .andExpect(jsonPath("$.phone").value("+7 900 777-88-99"))
                .andExpect(jsonPath("$.organization").value("ООО Обновленная"))
                .andExpect(jsonPath("$.isActive").value(false));
    }

    @Test
    void updateCustomer_Conflict_EmailExists() throws Exception {
        CustomerUpdateDto dto = new CustomerUpdateDto();
        dto.setEmail("customer2@test.com");
        dto.setFullName("Клиент 1");
        dto.setPhone("+7 900 111-22-33");
        dto.setOrganization("ООО Тест 1");
        dto.setIsActive(true);

        mockMvc.perform(put("/api/admin/customers/{id}", testUser1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    void updateCustomer_NotFound() throws Exception {
        CustomerUpdateDto dto = new CustomerUpdateDto();
        dto.setEmail("test@test.com");
        dto.setFullName("Test");
        dto.setPhone("+7 900 000-00-00");
        dto.setOrganization("Test Org");
        dto.setIsActive(true);

        UUID nonExistentId = UUID.randomUUID();
        mockMvc.perform(put("/api/admin/customers/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }

    /* =======================
       DELETE /api/admin/customers/{id}
       ======================= */

    @Test
    @WithMockUser(roles = "admin")
    void deleteCustomer_Success() throws Exception {
        mockMvc.perform(delete("/api/admin/customers/{id}", testUser1.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/admin/customers/{id}", testUser1.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "admin")
    void deleteCustomer_NotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        mockMvc.perform(delete("/api/admin/customers/{id}", nonExistentId))
                .andExpect(status().isInternalServerError());
    }
}