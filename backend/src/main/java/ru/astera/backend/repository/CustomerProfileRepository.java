package ru.astera.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.astera.backend.entity.CustomerProfile;

import java.util.UUID;

@Repository
public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, UUID> {
    CustomerProfile findCustomerProfileByUserId(UUID userId);

    @Query("SELECT cp FROM CustomerProfile cp JOIN FETCH cp.user u WHERE u.role = 'customer'")
    Page<CustomerProfile> findAllCustomersWithUsers(Pageable pageable);
}