package ru.astera.backend.service;

import ru.astera.backend.dto.selection.ConfigurationCandidateDto;
import ru.astera.backend.dto.selection.ConfigurationComponentDto;

import java.util.List;
import java.util.UUID;

public interface ConfigCandidateService {
    List<ConfigurationCandidateDto> findByRequest(UUID requestId, boolean withComponents);

    ConfigurationCandidateDto get(UUID candidateId, boolean withComponents);

    void replaceCandidates(UUID requestId, List<ConfigurationCandidateDto> dtos);

    void deleteCandidate(UUID candidateId);

    List<ConfigurationComponentDto> getComponents(UUID candidateId);
}
