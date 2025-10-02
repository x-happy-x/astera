package ru.astera.backend.dto.selection;

import lombok.Getter;
import lombok.Setter;
import ru.astera.backend.entity.FuelType;

import java.math.BigDecimal;

@Setter
@Getter
public class SelectionQueryDto {
    private BigDecimal powerKw;
    private BigDecimal tIn;
    private BigDecimal tOut;
    private FuelType fuelType;

    private Integer topN = 3;
    private Boolean includeAutomation = true;
}
