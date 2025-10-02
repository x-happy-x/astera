package ru.astera.backend.service;

import org.springframework.stereotype.Service;
import ru.astera.backend.dto.selection.ConfigurationCandidateDto;
import ru.astera.backend.dto.selection.HeatingRequestDto;

import java.util.List;

@Service
public interface ConfigurationSelectionService {
    List<ConfigurationCandidateDto> selectTopConfigurations(HeatingRequestDto req, int topN, boolean includeAutomation);
}