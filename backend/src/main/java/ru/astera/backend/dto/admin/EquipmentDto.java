package ru.astera.backend.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.astera.backend.entity.EquipmentCategory;
import ru.astera.backend.entity.FuelType;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class EquipmentDto {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("category")
    private EquipmentCategory category;

    @JsonProperty("brand")
    private String brand;

    @JsonProperty("model")
    private String model;

    @JsonProperty("active")
    private Boolean active;

    @JsonProperty("powerMinKw")
    private BigDecimal powerMinKw;

    @JsonProperty("powerMaxKw")
    private BigDecimal powerMaxKw;

    @JsonProperty("flowMinM3h")
    private BigDecimal flowMinM3h;

    @JsonProperty("flowMaxM3h")
    private BigDecimal flowMaxM3h;

    @JsonProperty("dnSize")
    private Integer dnSize;

    @JsonProperty("fuelType")
    private FuelType fuelType;

    @JsonProperty("connectionKey")
    private String connectionKey;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("deliveryDays")
    private Integer deliveryDays;
}