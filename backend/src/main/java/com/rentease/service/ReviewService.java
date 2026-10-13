package com.rentease.service;

import com.rentease.dto.ReviewRequest;
import com.rentease.dto.ReviewResponse;
import com.rentease.entity.Property;
import com.rentease.entity.Review;
import com.rentease.entity.Role;
import com.rentease.entity.User;
import com.rentease.exception.BadRequestException;
import com.rentease.exception.ResourceNotFoundException;
import com.rentease.exception.UnauthorizedException;
import com.rentease.repository.PropertyRepository;
import com.rentease.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Transactional
    public ReviewResponse addReview(ReviewRequest request, User tenant) {
        if (tenant.getRole() != Role.ROLE_TENANT) {
            throw new UnauthorizedException("Only tenants can post property reviews");
        }

        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + request.getPropertyId()));

        if (property.getOwner().getId().equals(tenant.getId())) {
            throw new BadRequestException("Landlords cannot review their own properties");
        }

        Review review = new Review(property, tenant, request.getRating(), request.getComment());
        Review saved = reviewRepository.save(review);
        return ReviewResponse.fromEntity(saved);
    }

    public List<ReviewResponse> getPropertyReviews(Long propertyId) {
        return reviewRepository.findByPropertyIdOrderByCreatedAtDesc(propertyId)
                .stream()
                .map(ReviewResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteReview(Long reviewId, User user) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        if (!review.getTenant().getId().equals(user.getId()) && user.getRole() != Role.ROLE_ADMIN) {
            throw new UnauthorizedException("You are not authorized to delete this review");
        }

        reviewRepository.delete(review);
    }
}
