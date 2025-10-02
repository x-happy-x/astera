package ru.astera.backend.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record ConfigurationCandidateDto(
        UUID id,
        UUID requestId,
        BigDecimal totalPrice,
        String currency,
        Integer maxDeliveryDays,
        String connectionKey,
        Integer dnSize,
        List<ConfigurationComponentDto> components
) {
}
