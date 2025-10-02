package ru.astera.backend.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.astera.backend.entity.ConfigComponent;
import ru.astera.backend.entity.ConfigComponentId;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConfigComponentRepository extends JpaRepository<ConfigComponent, ConfigComponentId> {
    List<ConfigComponent> findByCandidateId(UUID candidateId);

    void deleteByCandidateId(UUID candidateId);
}
