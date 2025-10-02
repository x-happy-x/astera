package ru.astera.backend.dto.selection;

import lombok.Builder;
import ru.astera.backend.entity.FuelType;

import java.math.BigDecimal;

@Builder
public record HeatingRequestUpdateDto(
        BigDecimal powerKw,
        BigDecimal tIn,
        BigDecimal tOut,
        FuelType fuelType,
        String notes
) {
}
