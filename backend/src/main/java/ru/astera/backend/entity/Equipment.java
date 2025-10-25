package ru.astera.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "equipment",
        uniqueConstraints = @UniqueConstraint(name = "uq_equipment_brand_model", columnNames = {"brand", "model"}))
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "equipment_category")
    private EquipmentCategory category;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private boolean active = true;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type")
    private FuelType fuelType;

    @Column(name = "connection_key")
    private String connectionKey;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal price;

    @Column(name = "delivery_days")
    private Integer deliveryDays;

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) {
            return false;
        }
        Equipment equipment = (Equipment) o;
        return getId() != null && Objects.equals(getId(), equipment.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}