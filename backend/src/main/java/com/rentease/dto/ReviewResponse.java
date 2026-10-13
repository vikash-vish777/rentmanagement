package com.rentease.dto;

import com.rentease.entity.Review;
import java.time.LocalDateTime;

public class ReviewResponse {
    private Long id;
    private Long propertyId;
    private UserDto tenant;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

    public ReviewResponse() {
    }

    public static ReviewResponse fromEntity(Review review) {
        if (review == null) return null;
        ReviewResponse dto = new ReviewResponse();
        dto.setId(review.getId());
        if (review.getProperty() != null) {
            dto.setPropertyId(review.getProperty().getId());
        }
        dto.setTenant(UserDto.fromEntity(review.getTenant()));
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public UserDto getTenant() {
        return tenant;
    }

    public void setTenant(UserDto tenant) {
        this.tenant = tenant;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
