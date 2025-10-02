package ru.astera.backend.service;

import org.springframework.stereotype.Service;
import ru.astera.backend.dto.admin.*;

import java.util.UUID;

@Service
public interface EquipmentService {
    EquipmentPageDto getEquipmentWithPagination(int page, int size);
    EquipmentDto getEquipmentById(UUID id);
    EquipmentDto createEquipment(EquipmentCreateDto dto);
    EquipmentDto updateEquipment(UUID id, EquipmentUpdateDto dto);
    void deleteEquipment(UUID id);
}