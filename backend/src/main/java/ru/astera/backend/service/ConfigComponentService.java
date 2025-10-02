package ru.astera.backend.service;

import org.springframework.stereotype.Service;
import ru.astera.backend.dto.ConfigurationComponentDto;

import java.util.List;
import java.util.UUID;

@Service
public interface ConfigComponentService {
    List<ConfigurationComponentDto> findByCandidate(UUID candidateId);

    void replaceForCandidate(UUID candidateId, List<ConfigurationComponentDto> dtos);
}
