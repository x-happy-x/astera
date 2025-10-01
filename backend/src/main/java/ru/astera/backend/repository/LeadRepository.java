package ru.astera.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.astera.backend.entity.Lead;

import java.util.UUID;

@Repository
public interface LeadRepository extends JpaRepository<Lead, UUID> {
    
    boolean existsByEmailAndPhone(String email, String phone);
}