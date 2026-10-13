package com.rentease.controller;

import com.rentease.dto.ApiResponse;
import com.rentease.dto.PropertyImageDto;
import com.rentease.dto.PropertyRequest;
import com.rentease.dto.PropertyResponse;
import com.rentease.entity.FurnishedStatus;
import com.rentease.entity.PropertyStatus;
import com.rentease.entity.PropertyType;
import com.rentease.entity.User;
import com.rentease.service.AuthService;
import com.rentease.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<List<PropertyResponse>> getAllProperties() {
        return ResponseEntity.ok(propertyService.getAllProperties());
    }

    @GetMapping("/search")
    public ResponseEntity<List<PropertyResponse>> searchProperties(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) PropertyType propertyType,
            @RequestParam(required = false) Double minRent,
            @RequestParam(required = false) Double maxRent,
            @RequestParam(required = false) Integer bedrooms,
            @RequestParam(required = false) FurnishedStatus furnishedStatus
    ) {
        return ResponseEntity.ok(propertyService.searchProperties(
                city, area, propertyType, minRent, maxRent, bedrooms, furnishedStatus
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyResponse> getPropertyById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long currentUserId = null;
        if (authentication != null && authentication.isAuthenticated()) {
            User user = authService.getCurrentUserEntity(authentication.getName());
            if (user != null) {
                currentUserId = user.getId();
            }
        }
        return ResponseEntity.ok(propertyService.getPropertyById(id, currentUserId));
    }

    @GetMapping("/my-properties")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<List<PropertyResponse>> getMyProperties(Authentication authentication) {
        User user = authService.getCurrentUserEntity(authentication.getName());
        return ResponseEntity.ok(propertyService.getPropertiesByOwner(user.getId()));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<ApiResponse> createProperty(
            @Valid @RequestBody PropertyRequest request,
            Authentication authentication
    ) {
        User user = authService.getCurrentUserEntity(authentication.getName());
        PropertyResponse created = propertyService.createProperty(request, user);
        return ResponseEntity.ok(new ApiResponse(true, "Property listed successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<ApiResponse> updateProperty(
            @PathVariable Long id,
            @Valid @RequestBody PropertyRequest request,
            Authentication authentication
    ) {
        User user = authService.getCurrentUserEntity(authentication.getName());
        PropertyResponse updated = propertyService.updateProperty(id, request, user);
        return ResponseEntity.ok(new ApiResponse(true, "Property updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<ApiResponse> deleteProperty(
            @PathVariable Long id,
            Authentication authentication
    ) {
        User user = authService.getCurrentUserEntity(authentication.getName());
        propertyService.deleteProperty(id, user);
        return ResponseEntity.ok(new ApiResponse(true, "Property deleted successfully"));
    }

    @PostMapping("/{id}/images")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<ApiResponse> uploadImages(
            @PathVariable Long id,
            @RequestParam("files") List<MultipartFile> files,
            Authentication authentication
    ) {
        User user = authService.getCurrentUserEntity(authentication.getName());
        List<PropertyImageDto> uploaded = propertyService.addImages(id, files, user);
        return ResponseEntity.ok(new ApiResponse(true, "Images uploaded successfully", uploaded));
    }

    @DeleteMapping("/{id}/images/{imageId}")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<ApiResponse> deleteImage(
            @PathVariable Long id,
            @PathVariable Long imageId,
            Authentication authentication
    ) {
        User user = authService.getCurrentUserEntity(authentication.getName());
        propertyService.deleteImage(id, imageId, user);
        return ResponseEntity.ok(new ApiResponse(true, "Image deleted successfully"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<ApiResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam PropertyStatus status,
            Authentication authentication
    ) {
        User user = authService.getCurrentUserEntity(authentication.getName());
        PropertyResponse response = propertyService.changeStatus(id, status, user);
        return ResponseEntity.ok(new ApiResponse(true, "Property status updated", response));
    }
}
