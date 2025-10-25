package ru.astera.backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.astera.backend.entity.HeatingRequestStatus;
import ru.astera.backend.service.SelectionService;
import ru.astera.backend.service.impl.SelectionServiceImpl;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class SelectionController {

    private final SelectionService selectionService;

    // ---- Получить выбор по запросу
    @GetMapping("/heating-requests/{requestId}/selection")
    public ResponseEntity<SelectionServiceImpl.SelectionDto> getByRequest(@PathVariable UUID requestId) {
        return ResponseEntity.ok(selectionService.getByRequest(requestId));
    }

    // ---- Получить выбор по id
    @GetMapping("/selections/{selectionId}")
    public ResponseEntity<SelectionServiceImpl.SelectionDto> getById(@PathVariable UUID selectionId) {
        return ResponseEntity.ok(selectionService.getById(selectionId));
    }

    // ---- Зафиксировать выбор (create/update idempotent)
    @PostMapping("/heating-requests/{requestId}/selection")
    public ResponseEntity<SelectionServiceImpl.SelectionDto> select(
            @PathVariable UUID requestId,
            @RequestBody @Valid SelectRequest body
    ) {
        SelectionServiceImpl.SelectionDto dto = selectionService.select(requestId, body.candidateId(), body.pdfPath());
        return ResponseEntity.ok(dto);
    }

    // ---- Удалить выбор и вернуть статус запроса (опционально) к PROPOSED/CREATED
    @DeleteMapping("/heating-requests/{requestId}/selection")
    public ResponseEntity<Void> delete(
            @PathVariable UUID requestId,
            @RequestParam(required = false) HeatingRequestStatus statusAfterDelete
    ) {
        selectionService.deleteByRequest(requestId, statusAfterDelete);
        return ResponseEntity.noContent().build();
    }

    // ---- payload для выбора
    public record SelectRequest(
            @NotNull UUID candidateId,
            String pdfPath
    ) {
    }
}
