package com.rentease.repository;

import com.rentease.entity.FurnishedStatus;
import com.rentease.entity.Property;
import com.rentease.entity.PropertyStatus;
import com.rentease.entity.PropertyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    List<Property> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);

    List<Property> findByStatusOrderByCreatedAtDesc(PropertyStatus status);

    @Query("SELECT p FROM Property p WHERE " +
           "(:city IS NULL OR LOWER(p.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
           "(:area IS NULL OR LOWER(p.area) LIKE LOWER(CONCAT('%', :area, '%'))) AND " +
           "(:propertyType IS NULL OR p.propertyType = :propertyType) AND " +
           "(:minRent IS NULL OR p.rent >= :minRent) AND " +
           "(:maxRent IS NULL OR p.rent <= :maxRent) AND " +
           "(:bedrooms IS NULL OR p.bedrooms = :bedrooms) AND " +
           "(:furnishedStatus IS NULL OR p.furnishedStatus = :furnishedStatus) AND " +
           "(:status IS NULL OR p.status = :status) " +
           "ORDER BY p.createdAt DESC")
    List<Property> searchProperties(
            @Param("city") String city,
            @Param("area") String area,
            @Param("propertyType") PropertyType propertyType,
            @Param("minRent") Double minRent,
            @Param("maxRent") Double maxRent,
            @Param("bedrooms") Integer bedrooms,
            @Param("furnishedStatus") FurnishedStatus furnishedStatus,
            @Param("status") PropertyStatus status
    );

    long countByStatus(PropertyStatus status);

    @Query("SELECT p.city, COUNT(p) FROM Property p GROUP BY p.city ORDER BY COUNT(p) DESC")
    List<Object[]> countPropertiesByCity();

    @Query("SELECT AVG(p.rent) FROM Property p WHERE p.status = 'AVAILABLE'")
    Double findAverageRent();
}

