package ru.astera.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.astera.backend.dto.ConfigurationComponentDto;
import ru.astera.backend.entity.ConfigComponent;
import ru.astera.backend.mapper.ConfigComponentMapper;
import ru.astera.backend.repository.ConfigCandidateRepository;
import ru.astera.backend.repository.ConfigComponentRepository;
import ru.astera.backend.service.ConfigComponentService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConfigComponentServiceImpl implements ConfigComponentService {

    private final ConfigComponentRepository repo;
    private final ConfigCandidateRepository candidateRepo;
    private final ConfigComponentMapper mapper;

    @Transactional(readOnly = true)
    @Override
    public List<ConfigurationComponentDto> findByCandidate(UUID candidateId) {
        ensureCandidate(candidateId);
        return repo.findByCandidateId(candidateId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void replaceForCandidate(UUID candidateId, List<ConfigurationComponentDto> dtos) {
        ensureCandidate(candidateId);
        repo.deleteByCandidateId(candidateId);
        List<ConfigComponent> entities = dtos.stream()
                .map(mapper::toEntity)
                .peek(e -> candidateRepo.findById(candidateId).ifPresent(e::setCandidate))
                .toList();
        repo.saveAll(entities);
    }

    private void ensureCandidate(UUID candidateId) {
        if (!candidateRepo.existsById(candidateId)) {
            throw new NoSuchElementException("ConfigCandidate not found: " + candidateId);
        }
    }
}
