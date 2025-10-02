package ru.astera.backend.service;

import ru.astera.backend.dto.selection.ConfigurationCandidateDto;
import ru.astera.backend.dto.selection.HeatingRequestDto;

import java.util.List;

public interface ConfigurationSelectionService {
    List<ConfigurationCandidateDto> selectTopConfigurations(HeatingRequestDto req, int topN, boolean includeAutomation);
}