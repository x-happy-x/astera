package ru.astera.backend.dto.selection;

import lombok.Builder;
import ru.astera.backend.entity.EquipmentCategory;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record ConfigurationComponentDto(
        UUID equipmentId,
        EquipmentCategory category,
        String brand,
        String model,
        Integer dnSize,
        String connectionKey,
        Integer deliveryDays,
        BigDecimal qty,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {
}
