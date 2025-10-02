package ru.astera.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.astera.backend.dto.admin.EquipmentCreateDto;
import ru.astera.backend.dto.admin.EquipmentDto;
import ru.astera.backend.dto.admin.EquipmentPageDto;
import ru.astera.backend.dto.admin.EquipmentUpdateDto;
import ru.astera.backend.service.EquipmentService;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/equipment")
@RequiredArgsConstructor
public class AdminEquipmentController {

    private final EquipmentService equipmentService;

    @GetMapping
    public ResponseEntity<EquipmentPageDto> getEquipment(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        EquipmentPageDto equipment = equipmentService.getEquipmentWithPagination(page, size);
        return ResponseEntity.ok(equipment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipmentDto> getEquipment(@PathVariable UUID id) {
        EquipmentDto equipment = equipmentService.getEquipmentById(id);
        return ResponseEntity.ok(equipment);
    }

    @PostMapping
    public ResponseEntity<EquipmentDto> createEquipment(@Valid @RequestBody EquipmentCreateDto dto) {
        EquipmentDto equipment = equipmentService.createEquipment(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(equipment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EquipmentDto> updateEquipment(
            @PathVariable UUID id,
            @Valid @RequestBody EquipmentUpdateDto dto) {
        EquipmentDto equipment = equipmentService.updateEquipment(id, dto);
        return ResponseEntity.ok(equipment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEquipment(@PathVariable UUID id) {
        equipmentService.deleteEquipment(id);
        return ResponseEntity.noContent().build();
    }
}