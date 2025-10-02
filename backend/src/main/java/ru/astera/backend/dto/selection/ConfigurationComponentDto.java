package ru.astera.backend.dto.selection;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
public class ConfigurationComponentDto {
    private UUID equipmentId;
    private String category;      // 'boiler','burner','pump','valve','flowmeter','automation'
    private String brand;
    private String model;

    private BigDecimal qty;       // обычно 1
    private BigDecimal unitPrice; // цена за единицу
    private BigDecimal subtotal;  // qty * unitPrice
}
