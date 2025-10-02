package ru.astera.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.astera.backend.entity.CustomerProfile;

import java.util.UUID;

@Repository
public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, UUID> {
    CustomerProfile findCustomerProfileByUserId(UUID userId);
}