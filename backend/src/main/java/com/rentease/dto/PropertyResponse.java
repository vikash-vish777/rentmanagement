package com.rentease.dto;

import com.rentease.entity.FurnishedStatus;
import com.rentease.entity.Property;
import com.rentease.entity.PropertyStatus;
import com.rentease.entity.PropertyType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PropertyResponse {
    private Long id;
    private String title;
    private String description;
    private PropertyType propertyType;
    private Double rent;
    private Double securityDeposit;
    private String address;
    private String city;
    private String area;
    private String pincode;
    private Double latitude;
    private Double longitude;
    private Integer bedrooms;
    private Integer bathrooms;
    private Integer sqft;
    private FurnishedStatus furnishedStatus;
    private LocalDate availableFrom;
    private PropertyStatus status;
    private UserDto owner;
    private List<PropertyImageDto> images = new ArrayList<>();
    private Double averageRating;
    private Long totalReviews;
    private Boolean isFavorite;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PropertyResponse() {
    }

    public static PropertyResponse fromEntity(Property property) {
        if (property == null) return null;
        PropertyResponse dto = new PropertyResponse();
        dto.setId(property.getId());
        dto.setTitle(property.getTitle());
        dto.setDescription(property.getDescription());
        dto.setPropertyType(property.getPropertyType());
        dto.setRent(property.getRent());
        dto.setSecurityDeposit(property.getSecurityDeposit());
        dto.setAddress(property.getAddress());
        dto.setCity(property.getCity());
        dto.setArea(property.getArea());
        dto.setPincode(property.getPincode());
        dto.setLatitude(property.getLatitude());
        dto.setLongitude(property.getLongitude());
        dto.setBedrooms(property.getBedrooms());
        dto.setBathrooms(property.getBathrooms());
        dto.setSqft(property.getSqft());
        dto.setFurnishedStatus(property.getFurnishedStatus());
        dto.setAvailableFrom(property.getAvailableFrom());
        dto.setStatus(property.getStatus());
        dto.setCreatedAt(property.getCreatedAt());
        dto.setUpdatedAt(property.getUpdatedAt());

        if (property.getOwner() != null) {
            dto.setOwner(UserDto.fromEntity(property.getOwner()));
        }

        if (property.getImages() != null) {
            dto.setImages(property.getImages().stream()
                    .map(PropertyImageDto::fromEntity)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }

    public Double getRent() {
        return rent;
    }

    public void setRent(Double rent) {
        this.rent = rent;
    }

    public Double getSecurityDeposit() {
        return securityDeposit;
    }

    public void setSecurityDeposit(Double securityDeposit) {
        this.securityDeposit = securityDeposit;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(Integer bedrooms) {
        this.bedrooms = bedrooms;
    }

    public Integer getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(Integer bathrooms) {
        this.bathrooms = bathrooms;
    }

    public Integer getSqft() {
        return sqft;
    }

    public void setSqft(Integer sqft) {
        this.sqft = sqft;
    }

    public FurnishedStatus getFurnishedStatus() {
        return furnishedStatus;
    }

    public void setFurnishedStatus(FurnishedStatus furnishedStatus) {
        this.furnishedStatus = furnishedStatus;
    }

    public LocalDate getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(LocalDate availableFrom) {
        this.availableFrom = availableFrom;
    }

    public PropertyStatus getStatus() {
        return status;
    }

    public void setStatus(PropertyStatus status) {
        this.status = status;
    }

    public UserDto getOwner() {
        return owner;
    }

    public void setOwner(UserDto owner) {
        this.owner = owner;
    }

    public List<PropertyImageDto> getImages() {
        return images;
    }

    public void setImages(List<PropertyImageDto> images) {
        this.images = images;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Long getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(Long totalReviews) {
        this.totalReviews = totalReviews;
    }

    public Boolean getFavorite() {
        return isFavorite;
    }

    public void setFavorite(Boolean favorite) {
        isFavorite = favorite;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

