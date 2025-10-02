package ru.astera.backend.dto.selection;

import lombok.Builder;
import ru.astera.backend.entity.FuelType;

import java.math.BigDecimal;
import java.util.UUID;

@Builder(toBuilder = true)
public record HeatingRequestCreateDto(
        UUID customerId,
        BigDecimal powerKw,
        BigDecimal tIn,
        BigDecimal tOut,
        FuelType fuelType,
        String notes
) {
}
