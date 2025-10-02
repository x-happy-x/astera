package ru.astera.backend.mapper;

import org.springframework.stereotype.Component;
import ru.astera.backend.dto.HeatingRequestCreateDto;
import ru.astera.backend.dto.selection.HeatingRequestDto;
import ru.astera.backend.entity.HeatingRequest;

@Component
public class HeatingRequestMapper {
    public HeatingRequest toEntity(HeatingRequestCreateDto dto) {
        return HeatingRequest.builder()
                .powerKw(dto.powerKw())
                .tIn(dto.tIn())
                .tOut(dto.tOut())
                .fuelType(dto.fuelType())
                .build();
    }

    public HeatingRequestDto toDto(HeatingRequest r) {
        return HeatingRequestDto.builder()
                .id(r.getId())
                .powerKw(r.getPowerKw())
                .tIn(r.getTIn())
                .tOut(r.getTOut())
                .fuelType(r.getFuelType())
                .build();
    }
}
