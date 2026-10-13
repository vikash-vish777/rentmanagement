package com.rentease.controller;

import com.rentease.dto.*;
import com.rentease.entity.PropertyStatus;
import com.rentease.entity.User;
import com.rentease.service.AdminService;
import com.rentease.service.AuthService;
import com.rentease.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private AuthService authService;

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<ApiResponse> toggleUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody UserStatusUpdateRequest request
    ) {
        UserDto updated = adminService.toggleUserActiveStatus(id, request.getActive());
        return ResponseEntity.ok(new ApiResponse(true, "User status updated", updated));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse(true, "User deleted successfully"));
    }

    @GetMapping("/properties")
    public ResponseEntity<List<PropertyResponse>> getAllProperties() {
        return ResponseEntity.ok(adminService.getAllPropertiesForAdmin());
    }

    @PutMapping("/properties/{id}/status")
    public ResponseEntity<ApiResponse> updatePropertyStatus(
            @PathVariable Long id,
            @RequestParam PropertyStatus status
    ) {
        PropertyResponse response = adminService.updatePropertyStatus(id, status);
        return ResponseEntity.ok(new ApiResponse(true, "Property status updated to " + status, response));
    }

    @DeleteMapping("/properties/{id}")
    public ResponseEntity<ApiResponse> deleteProperty(
            @PathVariable Long id,
            Authentication authentication
    ) {
        User admin = authService.getCurrentUserEntity(authentication.getName());
        propertyService.deleteProperty(id, admin);
        return ResponseEntity.ok(new ApiResponse(true, "Property deleted successfully by Admin"));
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        return ResponseEntity.ok(adminService.getAllBookings());
    }
}
