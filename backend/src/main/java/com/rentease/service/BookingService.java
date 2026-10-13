package com.rentease.service;

import com.rentease.dto.BookingRequest;
import com.rentease.dto.BookingResponse;
import com.rentease.entity.*;
import com.rentease.exception.BadRequestException;
import com.rentease.exception.ResourceNotFoundException;
import com.rentease.exception.UnauthorizedException;
import com.rentease.repository.BookingRepository;
import com.rentease.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Transactional
    public BookingResponse createBooking(BookingRequest request, User tenant) {
        if (tenant.getRole() != Role.ROLE_TENANT) {
            throw new UnauthorizedException("Only tenants can book properties");
        }

        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + request.getPropertyId()));

        if (property.getStatus() != PropertyStatus.AVAILABLE) {
            throw new BadRequestException("Property is not available for booking");
        }

        if (property.getOwner().getId().equals(tenant.getId())) {
            throw new BadRequestException("You cannot book your own property");
        }

        Booking booking = new Booking(
                property,
                tenant,
                property.getOwner(),
                request.getMoveInDate(),
                request.getMessage()
        );

        Booking saved = bookingRepository.save(booking);
        return BookingResponse.fromEntity(saved);
    }

    public List<BookingResponse> getTenantBookings(Long tenantId) {
        return bookingRepository.findByTenantIdOrderByCreatedAtDesc(tenantId)
                .stream()
                .map(BookingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<BookingResponse> getLandlordBookings(Long landlordId) {
        return bookingRepository.findByLandlordIdOrderByCreatedAtDesc(landlordId)
                .stream()
                .map(BookingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public BookingResponse getBookingById(Long id, User user) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        if (!booking.getTenant().getId().equals(user.getId()) &&
            !booking.getLandlord().getId().equals(user.getId()) &&
            user.getRole() != Role.ROLE_ADMIN) {
            throw new UnauthorizedException("Not authorized to view this booking");
        }

        return BookingResponse.fromEntity(booking);
    }

    @Transactional
    public BookingResponse updateBookingStatus(Long bookingId, BookingStatus newStatus, String landlordNote, User landlord) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getLandlord().getId().equals(landlord.getId()) && landlord.getRole() != Role.ROLE_ADMIN) {
            throw new UnauthorizedException("Only the landlord or admin can update this booking status");
        }

        booking.setStatus(newStatus);
        if (landlordNote != null && !landlordNote.trim().isEmpty()) {
            booking.setLandlordNote(landlordNote.trim());
        }
        booking.setUpdatedAt(LocalDateTime.now());

        if (newStatus == BookingStatus.ACCEPTED || newStatus == BookingStatus.CONFIRMED) {
            Property property = booking.getProperty();
            property.setStatus(PropertyStatus.RENTED);
            propertyRepository.save(property);
        }

        Booking updated = bookingRepository.save(booking);
        return BookingResponse.fromEntity(updated);
    }

    @Transactional
    public BookingResponse cancelBooking(Long bookingId, User tenant) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getTenant().getId().equals(tenant.getId()) && tenant.getRole() != Role.ROLE_ADMIN) {
            throw new UnauthorizedException("Only the tenant who created the booking can cancel it");
        }

        if (booking.getStatus() == BookingStatus.REJECTED || booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Booking is already " + booking.getStatus());
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setUpdatedAt(LocalDateTime.now());

        // Free up property status if it was reserved
        Property property = booking.getProperty();
        if (property.getStatus() == PropertyStatus.RENTED) {
            property.setStatus(PropertyStatus.AVAILABLE);
            propertyRepository.save(property);
        }

        Booking updated = bookingRepository.save(booking);
        return BookingResponse.fromEntity(updated);
    }
}
