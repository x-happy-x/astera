package ru.astera.backend.mapper;

import org.springframework.stereotype.Component;
import ru.astera.backend.dto.ConfigurationCandidateDto;
import ru.astera.backend.entity.ConfigCandidate;

@Component
public class ConfigCandidateMapper {
    public ConfigurationCandidateDto toDtoWithoutComponents(ConfigCandidate candidate) {
        return null;
    }

    public ConfigurationCandidateDto toDto(ConfigCandidate configCandidate) {
        return null;
    }

    public ConfigCandidate toEntity(ConfigurationCandidateDto dto) {
        return null;
    }
}
