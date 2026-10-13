package com.rentease.service;

import com.rentease.dto.AdminStatsResponse;
import com.rentease.dto.BookingResponse;
import com.rentease.dto.PropertyResponse;
import com.rentease.dto.UserDto;
import com.rentease.entity.*;
import com.rentease.exception.BadRequestException;
import com.rentease.exception.ResourceNotFoundException;
import com.rentease.repository.BookingRepository;
import com.rentease.repository.PropertyRepository;
import com.rentease.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private BookingRepository bookingRepository;

    public AdminStatsResponse getDashboardStats() {
        AdminStatsResponse stats = new AdminStatsResponse();
        stats.setTotalUsers(userRepository.count());
        stats.setTotalTenants(userRepository.countByRole(Role.ROLE_TENANT));
        stats.setTotalLandlords(userRepository.countByRole(Role.ROLE_LANDLORD));

        stats.setTotalProperties(propertyRepository.count());
        stats.setActiveProperties(propertyRepository.countByStatus(PropertyStatus.AVAILABLE));

        stats.setTotalBookings(bookingRepository.count());
        stats.setPendingBookings(bookingRepository.countByStatus(BookingStatus.PENDING));
        stats.setConfirmedBookings(bookingRepository.countByStatus(BookingStatus.ACCEPTED) +
                                   bookingRepository.countByStatus(BookingStatus.CONFIRMED));

        Double avgRent = propertyRepository.findAverageRent();
        stats.setAverageRent(avgRent != null ? Math.round(avgRent * 100.0) / 100.0 : 0.0);

        Map<String, Long> cityMap = new HashMap<>();
        List<Object[]> cityCounts = propertyRepository.countPropertiesByCity();
        for (Object[] row : cityCounts) {
            String city = (String) row[0];
            Long count = (Long) row[1];
            cityMap.put(city, count);
        }
        stats.setPropertiesByCity(cityMap);

        return stats;
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto toggleUserActiveStatus(Long userId, boolean active) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (user.getRole() == Role.ROLE_ADMIN) {
            throw new BadRequestException("Cannot disable administrator accounts");
        }

        user.setActive(active);
        User updated = userRepository.save(user);
        return UserDto.fromEntity(updated);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (user.getRole() == Role.ROLE_ADMIN) {
            throw new BadRequestException("Cannot delete administrator accounts");
        }

        userRepository.delete(user);
    }

    public List<PropertyResponse> getAllPropertiesForAdmin() {
        return propertyRepository.findAll().stream()
                .map(PropertyResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public PropertyResponse updatePropertyStatus(Long propertyId, PropertyStatus status) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));

        property.setStatus(status);
        property.setUpdatedAt(LocalDateTime.now());
        Property updated = propertyRepository.save(property);
        return PropertyResponse.fromEntity(updated);
    }

    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(BookingResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
