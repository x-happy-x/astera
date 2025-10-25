package ru.astera.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.astera.backend.entity.Selection;

import java.util.Optional;
import java.util.UUID;

public interface SelectionRepository extends JpaRepository<Selection, UUID> {
    Optional<Selection> findByRequest_Id(UUID requestId);
    boolean existsByRequest_Id(UUID requestId);
    void deleteByRequest_Id(UUID requestId);
}
