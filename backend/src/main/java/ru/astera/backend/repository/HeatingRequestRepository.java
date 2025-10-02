package ru.astera.backend.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.astera.backend.entity.FuelType;
import ru.astera.backend.entity.HeatingRequest;
import ru.astera.backend.entity.HeatingRequestStatus;

import java.util.UUID;

@Repository
public interface HeatingRequestRepository extends JpaRepository<HeatingRequest, UUID> {

    Page<HeatingRequest> findByCustomerProfile_UserId(UUID customerId, Pageable pageable);

    Page<HeatingRequest> findByStatus(HeatingRequestStatus status, Pageable pageable);

    @Query("""
            select r from HeatingRequest r
             where (:customerId is null or r.customerProfile.userId = :customerId)
               and (:status is null or r.status = :status)
               and (:fuelType is null or r.fuelType = :fuelType)
            """)
    Page<HeatingRequest> search(@Param("customerId") UUID customerId,
                                @Param("status") HeatingRequestStatus status,
                                @Param("fuelType") FuelType fuelType,
                                Pageable pageable);
}