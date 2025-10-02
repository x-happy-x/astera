package ru.astera.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.astera.backend.dto.selection.ConfigurationCandidateDto;
import ru.astera.backend.dto.selection.ConfigurationComponentDto;
import ru.astera.backend.service.ConfigCandidateService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class ConfigCandidatesController {

    private final ConfigCandidateService candidateService;

    /**
     * Получить кандидатов по запросу (состав опционально).
     */
    @GetMapping("/heating-requests/{requestId}/candidates")
    public ResponseEntity<List<ConfigurationCandidateDto>> getByRequest(
            @PathVariable UUID requestId,
            @RequestParam(defaultValue = "true") boolean withComponents
    ) {
        return ResponseEntity.ok(candidateService.findByRequest(requestId, withComponents));
    }

    /**
     * Заменить все кандидаты по запросу (идемпотентно): delete&insert.
     */
    @PutMapping("/heating-requests/{requestId}/candidates")
    public ResponseEntity<Void> replaceForRequest(
            @PathVariable UUID requestId,
            @RequestBody @Valid List<ConfigurationCandidateDto> candidates
    ) {
        candidateService.replaceCandidates(requestId, candidates);
        return ResponseEntity.noContent().build();
    }

    /**
     * Получить одного кандидата (состав опционально).
     */
    @GetMapping("/candidates/{candidateId}")
    public ResponseEntity<ConfigurationCandidateDto> getCandidate(
            @PathVariable UUID candidateId,
            @RequestParam(defaultValue = "true") boolean withComponents
    ) {
        return ResponseEntity.ok(candidateService.get(candidateId, withComponents));
    }

    /**
     * Удалить кандидата.
     */
    @DeleteMapping("/candidates/{candidateId}")
    public ResponseEntity<Void> deleteCandidate(@PathVariable UUID candidateId) {
        candidateService.deleteCandidate(candidateId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Получить компоненты кандидата.
     */
    @GetMapping("/candidates/{candidateId}/components")
    public ResponseEntity<List<ConfigurationComponentDto>> getComponents(@PathVariable UUID candidateId) {
        return ResponseEntity.ok(candidateService.getComponents(candidateId));
    }
}
