package ru.astera.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "selections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Selection {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "request_id", nullable = false)
    private HeatingRequest request;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidate_id", nullable = false)
    private ConfigCandidate candidate;

    @CreationTimestamp
    @Column(name = "selected_at", nullable = false)
    private OffsetDateTime selectedAt;

    @Column(name = "pdf_path")
    private String pdfPath;
}
