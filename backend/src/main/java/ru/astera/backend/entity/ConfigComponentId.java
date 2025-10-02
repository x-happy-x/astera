package ru.astera.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ConfigComponentId implements Serializable {
    @Column(name = "candidate_id", nullable = false)
    private UUID candidateId;

    @Column(name = "equipment_id", nullable = false)
    private UUID equipmentId;
}
