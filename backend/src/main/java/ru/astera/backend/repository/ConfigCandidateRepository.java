package ru.astera.backend.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.astera.backend.entity.ConfigCandidate;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConfigCandidateRepository extends JpaRepository<ConfigCandidate, UUID> {

    @EntityGraph(attributePaths = {"components"})
    List<ConfigCandidate> findByRequestIdOrderByTotalPriceAscCreatedAtAsc(UUID requestId);

    List<ConfigCandidate> findByRequestId(UUID requestId);

    @Modifying
    @Query("delete from ConfigCandidate c where c.request.id = :requestId")
    void deleteByRequestId(@Param("requestId") UUID requestId);
}