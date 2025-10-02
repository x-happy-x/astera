package ru.astera.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "equipment")
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "Category is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private EquipmentCategory category;

    @NotBlank(message = "Brand is required")
    @Column(name = "brand", nullable = false)
    private String brand;

    @NotBlank(message = "Model is required")
    @Column(name = "model", nullable = false)
    private String model;

    @NotNull(message = "Active status is required")
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "power_min_kw", precision = 10, scale = 2)
    private BigDecimal powerMinKw;

    @Column(name = "power_max_kw", precision = 10, scale = 2)
    private BigDecimal powerMaxKw;

    @Column(name = "flow_min_m3h", precision = 10, scale = 3)
    private BigDecimal flowMinM3h;

    @Column(name = "flow_max_m3h", precision = 10, scale = 3)
    private BigDecimal flowMaxM3h;

    @Column(name = "dn_size")
    private Integer dnSize;

    @Column(name = "fuel_type")
    private String fuelType;

    @Column(name = "connection_key")
    private String connectionKey;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    @Column(name = "price", nullable = false, precision = 14, scale = 2)
    private BigDecimal price;

    @Column(name = "delivery_days")
    private Integer deliveryDays;

    public Equipment() {}

    public Equipment(EquipmentCategory category, String brand, String model, BigDecimal price) {
        this.category = category;
        this.brand = brand;
        this.model = model;
        this.price = price;
    }

    public enum EquipmentCategory {
        boiler("Котел"),
        burner("Горелка"),
        pump("Насос"),
        valve("Клапан"),
        flowmeter("Расходомер"),
        automation("Автоматика");

        private final String displayName;

        EquipmentCategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

}