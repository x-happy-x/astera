package ru.astera.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.astera.backend.dto.admin.EquipmentCreateDto;
import ru.astera.backend.dto.admin.EquipmentDto;
import ru.astera.backend.dto.admin.EquipmentPageDto;
import ru.astera.backend.dto.admin.EquipmentUpdateDto;
import ru.astera.backend.entity.EquipmentCategory;
import ru.astera.backend.exception.EquipmentNotFoundException;
import ru.astera.backend.security.JwtAuthenticationFilter;
import ru.astera.backend.service.EquipmentService;
import ru.astera.backend.service.JwtService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminEquipmentController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        AdminEquipmentControllerTest.MockConfig.class
})
@WithMockUser(roles = "manager")
class AdminEquipmentControllerTest {

    @TestConfiguration
    static class MockConfig {
        @Bean
        EquipmentService equipmentService() {
            return Mockito.mock(EquipmentService.class);
        }

        @Bean
        JwtService jwtService() {
            return Mockito.mock(JwtService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getEquipment_ShouldReturnPaginatedEquipment() throws Exception {
        EquipmentDto equipment1 = createTestEquipmentDto(UUID.randomUUID(), "Bosch", "Model1");
        EquipmentDto equipment2 = createTestEquipmentDto(UUID.randomUUID(), "Viessmann", "Model2");

        EquipmentPageDto pageDto = new EquipmentPageDto();
        pageDto.setEquipment(Arrays.asList(equipment1, equipment2));
        pageDto.setTotalEquipment(2L);
        pageDto.setCurrentPage(0);
        pageDto.setTotalPages(1);
        pageDto.setPageSize(20);

        when(equipmentService.getEquipmentWithPagination(0, 20)).thenReturn(pageDto);

        mockMvc.perform(get("/api/admin/equipment")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.equipment").isArray())
                .andExpect(jsonPath("$.equipment.length()").value(2))
                .andExpect(jsonPath("$.totalEquipment").value(2))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.pageSize").value(20));
    }

    @Test
    void getEquipmentById_ShouldReturnEquipment() throws Exception {
        UUID equipmentId = UUID.randomUUID();
        EquipmentDto equipment = createTestEquipmentDto(equipmentId, "Bosch", "Model1");

        when(equipmentService.getEquipmentById(equipmentId)).thenReturn(equipment);

        mockMvc.perform(get("/api/admin/equipment/{id}", equipmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(equipmentId.toString()))
                .andExpect(jsonPath("$.brand").value("Bosch"))
                .andExpect(jsonPath("$.model").value("Model1"));
    }

    @Test
    void getEquipmentById_ShouldReturn404_WhenEquipmentNotFound() throws Exception {
        UUID equipmentId = UUID.randomUUID();

        when(equipmentService.getEquipmentById(equipmentId))
                .thenThrow(new EquipmentNotFoundException("Equipment not found"));

        mockMvc.perform(get("/api/admin/equipment/{id}", equipmentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void createEquipment_ShouldReturnCreatedEquipment() throws Exception {
        EquipmentCreateDto createDto = createTestEquipmentCreateDto();
        EquipmentDto createdEquipment = createTestEquipmentDto(UUID.randomUUID(), "Bosch", "Model1");

        when(equipmentService.createEquipment(any(EquipmentCreateDto.class))).thenReturn(createdEquipment);

        mockMvc.perform(post("/api/admin/equipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.brand").value("Bosch"))
                .andExpect(jsonPath("$.model").value("Model1"));
    }

    @Test
    void updateEquipment_ShouldReturnUpdatedEquipment() throws Exception {
        UUID equipmentId = UUID.randomUUID();
        EquipmentUpdateDto updateDto = createTestEquipmentUpdateDto();
        EquipmentDto updatedEquipment = createTestEquipmentDto(equipmentId, "Bosch", "UpdatedModel");

        when(equipmentService.updateEquipment(eq(equipmentId), any(EquipmentUpdateDto.class)))
                .thenReturn(updatedEquipment);

        mockMvc.perform(put("/api/admin/equipment/{id}", equipmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model").value("UpdatedModel"));
    }

    @Test
    void deleteEquipment_ShouldReturn204() throws Exception {
        UUID equipmentId = UUID.randomUUID();

        mockMvc.perform(delete("/api/admin/equipment/{id}", equipmentId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteEquipment_ShouldReturn404_WhenEquipmentNotFound() throws Exception {
        UUID equipmentId = UUID.randomUUID();

        doThrow(new EquipmentNotFoundException("Equipment not found"))
                .when(equipmentService).deleteEquipment(equipmentId);

        mockMvc.perform(delete("/api/admin/equipment/{id}", equipmentId))
                .andExpect(status().isNotFound());
    }

    private EquipmentDto createTestEquipmentDto(UUID id, String brand, String model) {
        EquipmentDto dto = new EquipmentDto();
        dto.setId(id);
        dto.setCategory(EquipmentCategory.boiler);
        dto.setBrand(brand);
        dto.setModel(model);
        dto.setActive(true);
        dto.setPowerMinKw(new BigDecimal("10.0"));
        dto.setPowerMaxKw(new BigDecimal("50.0"));
        dto.setDnSize(80);
        dto.setConnectionKey("DN80_GAS_STD");
        dto.setPrice(new BigDecimal("25000.00"));
        dto.setDeliveryDays(14);
        return dto;
    }

    private EquipmentCreateDto createTestEquipmentCreateDto() {
        EquipmentCreateDto dto = new EquipmentCreateDto();
        dto.setCategory(EquipmentCategory.boiler);
        dto.setBrand("Bosch");
        dto.setModel("Model1");
        dto.setActive(true);
        dto.setPowerMinKw(new BigDecimal("10.0"));
        dto.setPowerMaxKw(new BigDecimal("50.0"));
        dto.setDnSize(80);
        dto.setConnectionKey("DN80_GAS_STD");
        dto.setPrice(new BigDecimal("25000.00"));
        dto.setDeliveryDays(14);
        return dto;
    }

    private EquipmentUpdateDto createTestEquipmentUpdateDto() {
        EquipmentUpdateDto dto = new EquipmentUpdateDto();
        dto.setCategory(EquipmentCategory.boiler);
        dto.setBrand("Bosch");
        dto.setModel("UpdatedModel");
        dto.setActive(true);
        dto.setPowerMinKw(new BigDecimal("10.0"));
        dto.setPowerMaxKw(new BigDecimal("50.0"));
        dto.setDnSize(80);
        dto.setConnectionKey("DN80_GAS_STD");
        dto.setPrice(new BigDecimal("25000.00"));
        dto.setDeliveryDays(14);
        return dto;
    }
}