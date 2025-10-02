package ru.astera.backend.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.astera.backend.dto.selection.ConfigurationCandidateDto;
import ru.astera.backend.dto.selection.HeatingRequestDto;
import ru.astera.backend.service.ConfigCandidateService;
import ru.astera.backend.service.ConfigurationSelectionService;
import ru.astera.backend.service.HeatingRequestService;

import java.util.*;

@RestController
@RequestMapping("/api/heating-requests")
@RequiredArgsConstructor
@Validated
public class SelectionController {

    private final HeatingRequestService heatingRequestService;
    private final ConfigurationSelectionService selectionService;
    private final ConfigCandidateService candidateService;

    /**
     * Превью (без сохранения): посчитать top-N конфигураций для запроса.
     */
    @PostMapping("/{id}/preview-candidates")
    public ResponseEntity<List<ConfigurationCandidateDto>> preview(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "5") @Min(1) int topN,
            @RequestParam(defaultValue = "true") boolean includeAutomation
    ) {
        HeatingRequestDto req = heatingRequestService.get(id);
        List<ConfigurationCandidateDto> preview =
                selectionService.selectTopConfigurations(req, topN, includeAutomation);
        return ResponseEntity.ok(preview);
    }

    /**
     * Генерация и сохранение: заменить кандидатов у запроса на top-N из движка,
     * вернуть сохранённые (уже с ID из базы).
     */
    @PostMapping("/{id}/generate-candidates")
    public ResponseEntity<List<ConfigurationCandidateDto>> generateAndPersist(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "5") @Min(1) int topN,
            @RequestParam(defaultValue = "true") boolean includeAutomation
    ) {
        HeatingRequestDto req = heatingRequestService.get(id);
        List<ConfigurationCandidateDto> generated =
                selectionService.selectTopConfigurations(req, topN, includeAutomation);

        // сохранить как "текущую выдачу" (Форма №3)
        candidateService.replaceCandidates(id, generated);

        // вернуть уже сохранённые кандидаты (с компонентами и id)
        return ResponseEntity.ok(candidateService.findByRequest(id, true));
    }
}
