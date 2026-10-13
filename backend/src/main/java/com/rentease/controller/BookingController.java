package com.rentease.controller;

import com.rentease.dto.ApiResponse;
import com.rentease.dto.BookingRequest;
import com.rentease.dto.BookingResponse;
import com.rentease.dto.BookingStatusUpdateRequest;
import com.rentease.entity.User;
import com.rentease.service.AuthService;
import com.rentease.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private AuthService authService;

    @PostMapping
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<ApiResponse> createBooking(
            @Valid @RequestBody BookingRequest request,
            Authentication authentication
    ) {
        User user = authService.getCurrentUserEntity(authentication.getName());
        BookingResponse booking = bookingService.createBooking(request, user);
        return ResponseEntity.ok(new ApiResponse(true, "Booking request submitted successfully", booking));
    }

    @GetMapping("/my-bookings")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<List<BookingResponse>> getMyBookings(Authentication authentication) {
        User user = authService.getCurrentUserEntity(authentication.getName());
        return ResponseEntity.ok(bookingService.getTenantBookings(user.getId()));
    }

    @GetMapping("/landlord")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<List<BookingResponse>> getLandlordBookings(Authentication authentication) {
        User user = authService.getCurrentUserEntity(authentication.getName());
        return ResponseEntity.ok(bookingService.getLandlordBookings(user.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        User user = authService.getCurrentUserEntity(authentication.getName());
        return ResponseEntity.ok(bookingService.getBookingById(id, user));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<ApiResponse> updateBookingStatus(
            @PathVariable Long id,
            @Valid @RequestBody BookingStatusUpdateRequest request,
            Authentication authentication
    ) {
        User user = authService.getCurrentUserEntity(authentication.getName());
        BookingResponse updated = bookingService.updateBookingStatus(
                id, request.getStatus(), request.getLandlordNote(), user
        );
        return ResponseEntity.ok(new ApiResponse(true, "Booking status updated to " + request.getStatus(), updated));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('TENANT', 'ADMIN')")
    public ResponseEntity<ApiResponse> cancelBooking(
            @PathVariable Long id,
            Authentication authentication
    ) {
        User user = authService.getCurrentUserEntity(authentication.getName());
        BookingResponse cancelled = bookingService.cancelBooking(id, user);
        return ResponseEntity.ok(new ApiResponse(true, "Booking cancelled successfully", cancelled));
    }
}
