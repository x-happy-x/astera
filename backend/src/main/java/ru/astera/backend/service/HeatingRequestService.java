package ru.astera.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.astera.backend.dto.HeatingRequestCreateDto;
import ru.astera.backend.dto.HeatingRequestUpdateDto;
import ru.astera.backend.dto.selection.HeatingRequestDto;
import ru.astera.backend.entity.FuelType;
import ru.astera.backend.entity.HeatingRequestStatus;

import java.util.UUID;

@Service
public interface HeatingRequestService {
    HeatingRequestDto create(HeatingRequestCreateDto dto);

    HeatingRequestDto get(UUID id);

    Page<HeatingRequestDto> list(UUID customerId,
                                 HeatingRequestStatus status,
                                 FuelType fuelType,
                                 Pageable pageable);

    HeatingRequestDto updateParams(UUID id, HeatingRequestUpdateDto dto);

    HeatingRequestDto setStatus(UUID id, HeatingRequestStatus status);

    void delete(UUID id);
}
