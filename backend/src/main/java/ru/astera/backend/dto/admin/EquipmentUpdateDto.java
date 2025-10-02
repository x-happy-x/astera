package ru.astera.backend.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import ru.astera.backend.entity.EquipmentCategory;
import ru.astera.backend.entity.FuelType;

import java.math.BigDecimal;

@Data
public class EquipmentUpdateDto {
    @NotNull(message = "Category is required")
    @JsonProperty("category")
    private EquipmentCategory category;

    @NotBlank(message = "Brand is required")
    @JsonProperty("brand")
    private String brand;

    @NotBlank(message = "Model is required")
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

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("deliveryDays")
    private Integer deliveryDays;
}