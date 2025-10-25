package ru.astera.backend.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.astera.backend.dto.selection.ConfigurationCandidateDto;
import ru.astera.backend.dto.selection.HeatingRequestDto;
import ru.astera.backend.service.ConfigCandidateService;
import ru.astera.backend.service.ConfigurationSelectionService;
import ru.astera.backend.service.HeatingRequestService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class ConfigurationController {

    private final HeatingRequestService heatingRequestService;
    private final ConfigurationSelectionService configurationService;
    private final ConfigCandidateService candidateService;

    /**
     * Генерация и сохранение: заменить кандидатов у запроса на top-N из движка,
     * вернуть сохранённые (уже с ID из базы).
     */
    @PostMapping("/heating-requests/{id}/generate-candidates")
    public synchronized ResponseEntity<List<ConfigurationCandidateDto>> generateAndPersist(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "3") @Min(1) int topN,
            @RequestParam(defaultValue = "true") boolean includeAutomation
    ) {
        // Сначала проверяем существующие кандидаты
        List<ConfigurationCandidateDto> candidates = candidateService.findByRequest(id, true);

        // Генерируем только если их действительно нет
        if (candidates.isEmpty()) {
            HeatingRequestDto req = heatingRequestService.get(id);
            candidates = configurationService.selectTopConfigurations(req, topN, includeAutomation);
            candidateService.replaceCandidates(id, candidates);
            // Перечитываем из базы чтобы получить актуальные ID
            candidates = candidateService.findByRequest(id, true);
        }

        return ResponseEntity.ok(candidates);
    }
}
