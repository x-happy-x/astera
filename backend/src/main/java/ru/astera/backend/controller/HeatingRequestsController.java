package ru.astera.backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.astera.backend.dto.selection.HeatingRequestCreateDto;
import ru.astera.backend.dto.selection.HeatingRequestDto;
import ru.astera.backend.dto.selection.HeatingRequestUpdateDto;
import ru.astera.backend.entity.FuelType;
import ru.astera.backend.entity.HeatingRequestStatus;
import ru.astera.backend.security.UserDetails;
import ru.astera.backend.service.HeatingRequestService;

import java.util.UUID;

@RestController
@RequestMapping("/api/heating-requests")
@RequiredArgsConstructor
@Validated
public class HeatingRequestsController {

    private final HeatingRequestService service;

    @PostMapping
    public ResponseEntity<HeatingRequestDto> create(
            @RequestBody @Valid HeatingRequestCreateDto body,
            @AuthenticationPrincipal UserDetails me
    ) {
        return ResponseEntity.ok(service.create(me.userId(), body));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HeatingRequestDto> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<HeatingRequestDto>> list(
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) HeatingRequestStatus status,
            @RequestParam(required = false) FuelType fuelType,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(service.list(customerId, status, fuelType, pageable));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<HeatingRequestDto> updateParams(
            @PathVariable UUID id,
            @RequestBody @Valid HeatingRequestUpdateDto body
    ) {
        return ResponseEntity.ok(service.updateParams(id, body));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<HeatingRequestDto> setStatus(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateStatusRequest body
    ) {
        return ResponseEntity.ok(service.setStatus(id, body.status()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    public record UpdateStatusRequest(HeatingRequestStatus status) {
    }
}