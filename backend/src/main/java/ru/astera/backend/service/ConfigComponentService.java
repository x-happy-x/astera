package ru.astera.backend.service;

import ru.astera.backend.dto.selection.ConfigurationComponentDto;

import java.util.List;
import java.util.UUID;

public interface ConfigComponentService {
    List<ConfigurationComponentDto> findByCandidate(UUID candidateId);

    void replaceForCandidate(UUID candidateId, List<ConfigurationComponentDto> dtos);
}
