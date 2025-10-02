package ru.astera.backend.dto;

import lombok.Builder;
import ru.astera.backend.entity.FuelType;
import ru.astera.backend.entity.HeatingRequestStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record HeatingRequestDto(
        UUID id,
        UUID customerId,
        BigDecimal powerKw,
        BigDecimal tIn,
        BigDecimal tOut,
        FuelType fuelType,
        HeatingRequestStatus status,
        String notes,
        OffsetDateTime createdAt
) {
}
