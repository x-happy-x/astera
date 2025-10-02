package ru.astera.backend.mapper;

import org.springframework.stereotype.Component;
import ru.astera.backend.dto.selection.FuelType;
import ru.astera.backend.dto.selection.HeatingRequestDto;
import ru.astera.backend.dto.selection.SelectionQueryDto;

@Component
public class SelectionMapper {

    public HeatingRequestDto toHeatingRequestDto(SelectionQueryDto q) {
        HeatingRequestDto dto = new HeatingRequestDto();
        dto.setPowerKw(q.getPowerKw());
        dto.setTIn(q.getTIn());
        dto.setTOut(q.getTOut());
        // безопасно приведём fuelType, если фронт прислал null — по умолчанию GAS
        dto.setFuelType(q.getFuelType() != null ? q.getFuelType() : FuelType.GAS);
        return dto;
    }
}
