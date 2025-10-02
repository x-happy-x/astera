package ru.astera.backend.mapper;

import org.springframework.stereotype.Component;
import ru.astera.backend.dto.selection.HeatingRequestDto;
import ru.astera.backend.dto.selection.SelectionQueryDto;
import ru.astera.backend.entity.FuelType;

@Component
public class SelectionMapper {

    public HeatingRequestDto toHeatingRequestDto(SelectionQueryDto q) {
        return HeatingRequestDto.builder()
                .powerKw(q.getPowerKw())
                .tIn(q.getTIn())
                .tOut(q.getTOut())
                .fuelType(q.getFuelType() != null ? q.getFuelType() : FuelType.gas)
                .build();
    }
}
