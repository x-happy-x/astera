package ru.astera.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.astera.backend.dto.admin.EquipmentCreateDto;
import ru.astera.backend.dto.admin.EquipmentDto;
import ru.astera.backend.dto.admin.EquipmentPageDto;
import ru.astera.backend.dto.admin.EquipmentUpdateDto;
import ru.astera.backend.entity.Equipment;
import ru.astera.backend.exception.EquipmentNotFoundException;
import ru.astera.backend.repository.EquipmentRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipmentServiceTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @InjectMocks
    private EquipmentServiceImpl equipmentService;

    private Equipment testEquipment;
    private UUID equipmentId;

    @BeforeEach
    void setUp() {
        equipmentId = UUID.randomUUID();
        testEquipment = createTestEquipment(equipmentId);
    }

    @Test
    void getEquipmentWithPagination_ShouldReturnPaginatedResult() {
        Equipment equipment1 = createTestEquipment(UUID.randomUUID());
        Equipment equipment2 = createTestEquipment(UUID.randomUUID());
        Page<Equipment> equipmentPage = new PageImpl<>(Arrays.asList(equipment1, equipment2));

        when(equipmentRepository.findAll(any(Pageable.class))).thenReturn(equipmentPage);

        EquipmentPageDto result = equipmentService.getEquipmentWithPagination(0, 20);

        assertNotNull(result);
        assertEquals(2, result.getEquipment().size());
        assertEquals(2L, result.getTotalEquipment());
        assertEquals(0, result.getCurrentPage());
        assertEquals(1, result.getTotalPages());
        assertEquals(20, result.getPageSize());
    }

    @Test
    void getEquipmentById_ShouldReturnEquipment_WhenExists() {
        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(testEquipment));

        EquipmentDto result = equipmentService.getEquipmentById(equipmentId);

        assertNotNull(result);
        assertEquals(equipmentId, result.getId());
        assertEquals("Bosch", result.getBrand());
        assertEquals("Model1", result.getModel());
        assertEquals(Equipment.EquipmentCategory.boiler, result.getCategory());
    }

    @Test
    void getEquipmentById_ShouldThrowException_WhenNotExists() {
        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.empty());

        assertThrows(EquipmentNotFoundException.class, () -> {
            equipmentService.getEquipmentById(equipmentId);
        });
    }

    @Test
    void createEquipment_ShouldSaveAndReturnEquipment() {
        EquipmentCreateDto createDto = createTestEquipmentCreateDto();
        when(equipmentRepository.save(any(Equipment.class))).thenReturn(testEquipment);

        EquipmentDto result = equipmentService.createEquipment(createDto);

        assertNotNull(result);
        assertEquals("Bosch", result.getBrand());
        assertEquals("Model1", result.getModel());
        assertEquals(Equipment.EquipmentCategory.boiler, result.getCategory());
        verify(equipmentRepository, times(1)).save(any(Equipment.class));
    }

    @Test
    void updateEquipment_ShouldUpdateAndReturnEquipment_WhenExists() {
        EquipmentUpdateDto updateDto = createTestEquipmentUpdateDto();
        updateDto.setModel("UpdatedModel");

        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(testEquipment));
        when(equipmentRepository.save(any(Equipment.class))).thenReturn(testEquipment);

        EquipmentDto result = equipmentService.updateEquipment(equipmentId, updateDto);

        assertNotNull(result);
        assertEquals("UpdatedModel", result.getModel());
        verify(equipmentRepository, times(1)).save(testEquipment);
    }

    @Test
    void updateEquipment_ShouldThrowException_WhenNotExists() {
        EquipmentUpdateDto updateDto = createTestEquipmentUpdateDto();
        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.empty());

        assertThrows(EquipmentNotFoundException.class, () -> {
            equipmentService.updateEquipment(equipmentId, updateDto);
        });
    }

    @Test
    void deleteEquipment_ShouldDeleteEquipment_WhenExists() {
        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(testEquipment));

        assertDoesNotThrow(() -> {
            equipmentService.deleteEquipment(equipmentId);
        });

        verify(equipmentRepository, times(1)).delete(testEquipment);
    }

    @Test
    void deleteEquipment_ShouldThrowException_WhenNotExists() {
        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.empty());

        assertThrows(EquipmentNotFoundException.class, () -> {
            equipmentService.deleteEquipment(equipmentId);
        });
    }

    private Equipment createTestEquipment(UUID id) {
        Equipment equipment = new Equipment();
        equipment.setId(id);
        equipment.setCategory(Equipment.EquipmentCategory.boiler);
        equipment.setBrand("Bosch");
        equipment.setModel("Model1");
        equipment.setActive(true);
        equipment.setPowerMinKw(new BigDecimal("10.0"));
        equipment.setPowerMaxKw(new BigDecimal("50.0"));
        equipment.setDnSize(80);
        equipment.setConnectionKey("DN80_GAS_STD");
        equipment.setPrice(new BigDecimal("25000.00"));
        equipment.setDeliveryDays(14);
        return equipment;
    }

    private EquipmentCreateDto createTestEquipmentCreateDto() {
        EquipmentCreateDto dto = new EquipmentCreateDto();
        dto.setCategory(Equipment.EquipmentCategory.boiler);
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
        dto.setCategory(Equipment.EquipmentCategory.boiler);
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
}