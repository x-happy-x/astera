package ru.astera.backend.service;

import org.springframework.stereotype.Service;
import ru.astera.backend.dto.ConfigurationCandidateDto;
import ru.astera.backend.dto.ConfigurationComponentDto;

import java.util.List;
import java.util.UUID;

@Service
public interface ConfigCandidateService {
    List<ConfigurationCandidateDto> findByRequest(UUID requestId, boolean withComponents);

    ConfigurationCandidateDto get(UUID candidateId, boolean withComponents);

    void replaceCandidates(UUID requestId, List<ConfigurationCandidateDto> dtos);

    void deleteCandidate(UUID candidateId);

    List<ConfigurationComponentDto> getComponents(UUID candidateId);
}
