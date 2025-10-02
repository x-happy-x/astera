package ru.astera.backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.astera.backend.dto.selection.ConfigurationCandidateDto;
import ru.astera.backend.dto.selection.HeatingRequestDto;
import ru.astera.backend.dto.selection.SelectionQueryDto;
import ru.astera.backend.mapper.SelectionMapper;
import ru.astera.backend.service.ConfigurationSelectionService;

import java.util.List;

@RestController
@RequestMapping("/api/selection")
@Validated
public class ConfigurationSelectionController {

    private final ConfigurationSelectionService selectionService;
    private final SelectionMapper selectionMapper;

    public ConfigurationSelectionController(ConfigurationSelectionService selectionService,
                                            SelectionMapper selectionMapper) {
        this.selectionService = selectionService;
        this.selectionMapper = selectionMapper;
    }

    @PostMapping("/configurations")
    public ResponseEntity<List<ConfigurationCandidateDto>> select(@Valid @RequestBody SelectionQueryDto query) {
        HeatingRequestDto req = selectionMapper.toHeatingRequestDto(query);
        int topN = query.getTopN() != null ? query.getTopN() : 5;
        boolean includeAutomation = query.getIncludeAutomation() == null || query.getIncludeAutomation();

        List<ConfigurationCandidateDto> candidates =
                selectionService.selectTopConfigurations(req, topN, includeAutomation);

        return ResponseEntity.ok(candidates);
    }
}