package ru.astera.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.astera.backend.dto.selection.HeatingRequestCreateDto;
import ru.astera.backend.dto.selection.HeatingRequestDto;
import ru.astera.backend.dto.selection.HeatingRequestUpdateDto;
import ru.astera.backend.entity.FuelType;
import ru.astera.backend.entity.HeatingRequest;
import ru.astera.backend.entity.HeatingRequestStatus;
import ru.astera.backend.mapper.HeatingRequestMapper;
import ru.astera.backend.repository.HeatingRequestRepository;
import ru.astera.backend.service.HeatingRequestService;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HeatingRequestServiceImpl implements HeatingRequestService {

    private final HeatingRequestRepository repo;
    private final HeatingRequestMapper mapper;

    @Transactional
    @Override
    public HeatingRequestDto create(HeatingRequestCreateDto dto) {
        validateParams(dto.powerKw(), dto.tIn(), dto.tOut());
        HeatingRequest entity = mapper.toEntity(dto);
        entity.setStatus(HeatingRequestStatus.CREATED);
        return mapper.toDto(repo.save(entity));
    }

    @Transactional(readOnly = true)
    @Override
    public HeatingRequestDto get(UUID id) {
        HeatingRequest r = repo.findById(id).orElseThrow(() -> notFound(id));
        return mapper.toDto(r);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<HeatingRequestDto> list(UUID customerId,
                                        HeatingRequestStatus status,
                                        FuelType fuelType,
                                        Pageable pageable) {
        return repo.search(customerId, status, fuelType, pageable).map(mapper::toDto);
    }

    @Transactional
    @Override
    public HeatingRequestDto updateParams(UUID id, HeatingRequestUpdateDto dto) {
        HeatingRequest r = repo.findById(id).orElseThrow(() -> notFound(id));
        if (dto.powerKw() != null || dto.tIn() != null || dto.tOut() != null) {
            BigDecimal power = dto.powerKw() != null ? dto.powerKw() : r.getPowerKw();
            BigDecimal tIn = dto.tIn() != null ? dto.tIn() : r.getTIn();
            BigDecimal tOut = dto.tOut() != null ? dto.tOut() : r.getTOut();
            validateParams(power, tIn, tOut);
            r.setPowerKw(power);
            r.setTIn(tIn);
            r.setTOut(tOut);
        }
        if (dto.fuelType() != null) {
            r.setFuelType(dto.fuelType());
        }
        if (dto.notes() != null) {
            r.setNotes(dto.notes());
        }
        return mapper.toDto(repo.save(r));
    }

    @Transactional
    @Override
    public HeatingRequestDto setStatus(UUID id, HeatingRequestStatus status) {
        HeatingRequest r = repo.findById(id).orElseThrow(() -> notFound(id));
        r.setStatus(status);
        return mapper.toDto(repo.save(r));
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        if (!repo.existsById(id)) {
            throw notFound(id);
        }
        repo.deleteById(id);
    }

    private void validateParams(BigDecimal powerKw, BigDecimal tIn, BigDecimal tOut) {
        if (powerKw == null || powerKw.signum() <= 0) {
            throw new IllegalArgumentException("power_kw должен быть > 0");
        }
        if (tIn == null || tOut == null || tIn.compareTo(tOut) <= 0) {
            throw new IllegalArgumentException("t_in должен быть больше t_out");
        }
    }

    private NoSuchElementException notFound(UUID id) {
        return new NoSuchElementException("HeatingRequest not found: " + id);
    }
}
