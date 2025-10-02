package ru.astera.backend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.astera.backend.dto.admin.EquipmentCreateDto;
import ru.astera.backend.dto.admin.EquipmentDto;
import ru.astera.backend.dto.admin.EquipmentPageDto;
import ru.astera.backend.dto.admin.EquipmentUpdateDto;
import ru.astera.backend.entity.Equipment;
import ru.astera.backend.exception.EquipmentNotFoundException;
import ru.astera.backend.repository.EquipmentRepository;
import ru.astera.backend.service.EquipmentService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentRepository equipmentRepository;

    @Override
    @Transactional(readOnly = true)
    public EquipmentPageDto getEquipmentWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("category").and(Sort.by("brand")).and(Sort.by("model")));
        Page<Equipment> equipmentPage = equipmentRepository.findAll(pageable);

        List<EquipmentDto> equipmentList = equipmentPage.getContent().stream()
                .map(this::convertToDto)
                .toList();

        EquipmentPageDto result = new EquipmentPageDto();
        result.setEquipment(equipmentList);
        result.setTotalEquipment(equipmentPage.getTotalElements());
        result.setCurrentPage(page);
        result.setTotalPages(equipmentPage.getTotalPages());
        result.setPageSize(size);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public EquipmentDto getEquipmentById(UUID id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new EquipmentNotFoundException("Equipment not found with id: " + id));
        return convertToDto(equipment);
    }

    @Override
    @Transactional
    public EquipmentDto createEquipment(EquipmentCreateDto dto) {
        Equipment equipment = convertFromCreateDto(dto);
        Equipment savedEquipment = equipmentRepository.save(equipment);
        log.info("Created equipment: {} {} with id: {}", savedEquipment.getBrand(), savedEquipment.getModel(), savedEquipment.getId());
        return convertToDto(savedEquipment);
    }

    @Override
    @Transactional
    public EquipmentDto updateEquipment(UUID id, EquipmentUpdateDto dto) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new EquipmentNotFoundException("Equipment not found with id: " + id));

        updateEquipmentFromDto(equipment, dto);
        Equipment updatedEquipment = equipmentRepository.save(equipment);
        log.info("Updated equipment: {} {} with id: {}", updatedEquipment.getBrand(), updatedEquipment.getModel(), updatedEquipment.getId());
        return convertToDto(updatedEquipment);
    }

    @Override
    @Transactional
    public void deleteEquipment(UUID id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new EquipmentNotFoundException("Equipment not found with id: " + id));

        equipmentRepository.delete(equipment);
        log.info("Deleted equipment: {} {} with id: {}", equipment.getBrand(), equipment.getModel(), equipment.getId());
    }

    private EquipmentDto convertToDto(Equipment equipment) {
        EquipmentDto dto = new EquipmentDto();
        dto.setId(equipment.getId());
        dto.setCategory(equipment.getCategory());
        dto.setBrand(equipment.getBrand());
        dto.setModel(equipment.getModel());
        dto.setActive(equipment.isActive());
        dto.setPowerMinKw(equipment.getPowerMinKw());
        dto.setPowerMaxKw(equipment.getPowerMaxKw());
        dto.setFlowMinM3h(equipment.getFlowMinM3h());
        dto.setFlowMaxM3h(equipment.getFlowMaxM3h());
        dto.setDnSize(equipment.getDnSize());
        dto.setFuelType(equipment.getFuelType());
        dto.setConnectionKey(equipment.getConnectionKey());
        dto.setPrice(equipment.getPrice());
        dto.setDeliveryDays(equipment.getDeliveryDays());
        return dto;
    }

    private Equipment convertFromCreateDto(EquipmentCreateDto dto) {
        Equipment equipment = new Equipment();
        equipment.setCategory(dto.getCategory());
        equipment.setBrand(dto.getBrand());
        equipment.setModel(dto.getModel());
        equipment.setActive(dto.getActive());
        equipment.setPowerMinKw(dto.getPowerMinKw());
        equipment.setPowerMaxKw(dto.getPowerMaxKw());
        equipment.setFlowMinM3h(dto.getFlowMinM3h());
        equipment.setFlowMaxM3h(dto.getFlowMaxM3h());
        equipment.setDnSize(dto.getDnSize());
        equipment.setFuelType(dto.getFuelType());
        equipment.setConnectionKey(dto.getConnectionKey());
        equipment.setPrice(dto.getPrice());
        equipment.setDeliveryDays(dto.getDeliveryDays());
        return equipment;
    }

    private void updateEquipmentFromDto(Equipment equipment, EquipmentUpdateDto dto) {
        equipment.setCategory(dto.getCategory());
        equipment.setBrand(dto.getBrand());
        equipment.setModel(dto.getModel());
        equipment.setActive(dto.getActive());
        equipment.setPowerMinKw(dto.getPowerMinKw());
        equipment.setPowerMaxKw(dto.getPowerMaxKw());
        equipment.setFlowMinM3h(dto.getFlowMinM3h());
        equipment.setFlowMaxM3h(dto.getFlowMaxM3h());
        equipment.setDnSize(dto.getDnSize());
        equipment.setFuelType(dto.getFuelType());
        equipment.setConnectionKey(dto.getConnectionKey());
        equipment.setPrice(dto.getPrice());
        equipment.setDeliveryDays(dto.getDeliveryDays());
    }
}