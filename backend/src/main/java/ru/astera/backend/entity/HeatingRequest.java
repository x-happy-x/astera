package ru.astera.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "heating_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HeatingRequest {
    @Id
    @Column(nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerProfile customerProfile;

    @Column(name = "power_kw", nullable = false, precision = 10, scale = 2)
    private BigDecimal powerKw;

    @Column(name = "t_in", nullable = false, precision = 5, scale = 2)
    private BigDecimal tIn;

    @Column(name = "t_out", nullable = false, precision = 5, scale = 2)
    private BigDecimal tOut;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type", nullable = false)
    private FuelType fuelType = FuelType.gas;

    @Column(columnDefinition = "text")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HeatingRequestStatus status = HeatingRequestStatus.CREATED;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}