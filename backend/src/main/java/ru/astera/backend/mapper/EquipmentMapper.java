package ru.astera.backend.mapper;

import org.springframework.stereotype.Component;
import ru.astera.backend.dto.selection.ConfigurationComponentDto;
import ru.astera.backend.entity.Equipment;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class EquipmentMapper {

    public ConfigurationComponentDto toComponentDto(Equipment e) {
        if (e == null) {
            return null;
        }
        ConfigurationComponentDto dto = new ConfigurationComponentDto();
        dto.setEquipmentId(e.getId());
        dto.setCategory(stringLower(e.getCategory()));
        dto.setBrand(e.getBrand());
        dto.setModel(e.getModel());
        dto.setQty(BigDecimal.ONE);
        BigDecimal unit = nz(e.getPrice());
        dto.setUnitPrice(unit);
        dto.setSubtotal(unit);
        return dto;
    }

    private String stringLower(Object category) {
        return category == null ? null : String.valueOf(category).toLowerCase();
    }

    private BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : v;
    }
}
