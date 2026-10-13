package com.rentease.repository;

import com.rentease.entity.Booking;
import com.rentease.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByTenantIdOrderByCreatedAtDesc(Long tenantId);
    List<Booking> findByLandlordIdOrderByCreatedAtDesc(Long landlordId);
    List<Booking> findByPropertyIdOrderByCreatedAtDesc(Long propertyId);
    long countByStatus(BookingStatus status);
    long countByLandlordIdAndStatus(Long landlordId, BookingStatus status);
}

