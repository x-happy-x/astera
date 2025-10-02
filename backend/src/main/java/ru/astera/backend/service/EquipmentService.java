package ru.astera.backend.service;

import ru.astera.backend.dto.admin.EquipmentCreateDto;
import ru.astera.backend.dto.admin.EquipmentDto;
import ru.astera.backend.dto.admin.EquipmentPageDto;
import ru.astera.backend.dto.admin.EquipmentUpdateDto;

import java.util.UUID;

public interface EquipmentService {
    EquipmentPageDto getEquipmentWithPagination(int page, int size);

    EquipmentDto getEquipmentById(UUID id);

    EquipmentDto createEquipment(EquipmentCreateDto dto);

    EquipmentDto updateEquipment(UUID id, EquipmentUpdateDto dto);

    void deleteEquipment(UUID id);
}