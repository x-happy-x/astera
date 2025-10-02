package ru.astera.backend.dto.selection;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
public class ConfigurationCandidateDto {
    private UUID requestId;               // может быть null
    private BigDecimal totalPrice;
    private String currency;              // "RUB"
    private Integer maxDeliveryDays;      // максимум по компонентам
    private String connectionKey;         // для котёл↔горелка
    private Integer dnSize;               // DN по котлу/линии

    private List<ConfigurationComponentDto> components = new ArrayList<>();

}
