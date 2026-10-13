package com.rentease.controller;

import com.rentease.dto.ApiResponse;
import com.rentease.dto.ReviewRequest;
import com.rentease.dto.ReviewResponse;
import com.rentease.entity.User;
import com.rentease.service.AuthService;
import com.rentease.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private AuthService authService;

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<ReviewResponse>> getPropertyReviews(@PathVariable Long propertyId) {
        return ResponseEntity.ok(reviewService.getPropertyReviews(propertyId));
    }

    @PostMapping
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<ApiResponse> addReview(
            @Valid @RequestBody ReviewRequest request,
            Authentication authentication
    ) {
        User user = authService.getCurrentUserEntity(authentication.getName());
        ReviewResponse review = reviewService.addReview(request, user);
        return ResponseEntity.ok(new ApiResponse(true, "Review submitted successfully", review));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteReview(
            @PathVariable Long id,
            Authentication authentication
    ) {
        User user = authService.getCurrentUserEntity(authentication.getName());
        reviewService.deleteReview(id, user);
        return ResponseEntity.ok(new ApiResponse(true, "Review deleted successfully"));
    }
}
