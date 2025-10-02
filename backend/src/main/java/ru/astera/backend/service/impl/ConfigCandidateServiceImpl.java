package ru.astera.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.astera.backend.dto.selection.ConfigurationCandidateDto;
import ru.astera.backend.dto.selection.ConfigurationComponentDto;
import ru.astera.backend.entity.ConfigCandidate;
import ru.astera.backend.entity.ConfigComponent;
import ru.astera.backend.mapper.ConfigCandidateMapper;
import ru.astera.backend.mapper.ConfigComponentMapper;
import ru.astera.backend.repository.ConfigCandidateRepository;
import ru.astera.backend.repository.ConfigComponentRepository;
import ru.astera.backend.repository.HeatingRequestRepository;
import ru.astera.backend.service.ConfigCandidateService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConfigCandidateServiceImpl implements ConfigCandidateService {

    private final ConfigCandidateRepository candidateRepo;
    private final ConfigComponentRepository componentRepo;
    private final HeatingRequestRepository requestRepo;

    private final ConfigCandidateMapper candidateMapper;
    private final ConfigComponentMapper componentMapper;

    @Transactional(readOnly = true)
    @Override
    public List<ConfigurationCandidateDto> findByRequest(UUID requestId, boolean withComponents) {
        ensureRequestExists(requestId);
        List<ConfigCandidate> list = withComponents
                ? candidateRepo.findByRequestIdOrderByTotalPriceAscCreatedAtAsc(requestId)
                : candidateRepo.findByRequestId(requestId);

        if (withComponents) {
            return list.stream().map(candidateMapper::toDto).collect(Collectors.toList());
        } else {
            return list.stream().map(candidateMapper::toDtoWithoutComponents).collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ConfigurationCandidateDto get(UUID candidateId, boolean withComponents) {
        ConfigCandidate c = candidateRepo.findById(candidateId)
                .orElseThrow(() -> new NoSuchElementException("ConfigCandidate not found: " + candidateId));
        if (withComponents) {
            // благодаря @EntityGraph в findById не подтянет компоненты,
            // поэтому при необходимости отдельно читаем:
            List<ConfigComponent> comps = componentRepo.findByCandidateId(candidateId);
            c.setComponents(comps);
        }
        return candidateMapper.toDto(c);
    }

    /**
     * Заменяет кандидатов по запросу на переданный список (идемпотентный сценарий показа Формы №3).
     * Реализация "delete & insert" в одной транзакции — безопасно для повторных запусков.
     */
    @Transactional
    @Override
    public void replaceCandidates(UUID requestId, List<ConfigurationCandidateDto> dtos) {
        ensureRequestExists(requestId);

        candidateRepo.deleteByRequestId(requestId);

        for (ConfigurationCandidateDto dto : dtos) {
            ConfigCandidate cand = candidateMapper.toEntity(dto);
            cand.setId(UUID.randomUUID());
            requestRepo.findById(requestId).ifPresent(cand::setRequest);
            cand = candidateRepo.save(cand);

            if (dto.components() != null && !dto.components().isEmpty()) {
                List<ConfigComponent> comps = new ArrayList<>(dto.components().size());
                for (ConfigurationComponentDto cDto : dto.components()) {
                    ConfigComponent comp = componentMapper.toEntity(cDto);
                    ConfigCandidate candidate = candidateRepo.findById(cand.getId()).orElse(null);
                    comp.setCandidate(candidate);
                    comps.add(comp);
                }
                componentRepo.saveAll(comps);
            }
        }
    }

    @Transactional
    @Override
    public void deleteCandidate(UUID candidateId) {
        if (!candidateRepo.existsById(candidateId)) {
            throw new NoSuchElementException("ConfigCandidate not found: " + candidateId);
        }
        candidateRepo.deleteById(candidateId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ConfigurationComponentDto> getComponents(UUID candidateId) {
        if (!candidateRepo.existsById(candidateId)) {
            throw new NoSuchElementException("ConfigCandidate not found: " + candidateId);
        }
        return componentRepo.findByCandidateId(candidateId).stream()
                .map(componentMapper::toDto)
                .collect(Collectors.toList());
    }

    private void ensureRequestExists(UUID requestId) {
        if (!requestRepo.existsById(requestId)) {
            throw new NoSuchElementException("HeatingRequest not found: " + requestId);
        }
    }
}
