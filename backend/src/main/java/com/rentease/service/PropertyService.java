package com.rentease.service;

import com.rentease.dto.PropertyImageDto;
import com.rentease.dto.PropertyRequest;
import com.rentease.dto.PropertyResponse;
import com.rentease.entity.*;
import com.rentease.exception.BadRequestException;
import com.rentease.exception.ResourceNotFoundException;
import com.rentease.exception.UnauthorizedException;
import com.rentease.repository.FavoriteRepository;
import com.rentease.repository.PropertyImageRepository;
import com.rentease.repository.PropertyRepository;
import com.rentease.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private PropertyImageRepository propertyImageRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private FileStorageService fileStorageService;

    public List<PropertyResponse> getAllProperties() {
        return propertyRepository.findByStatusOrderByCreatedAtDesc(PropertyStatus.AVAILABLE)
                .stream()
                .map(this::enrichPropertyResponse)
                .collect(Collectors.toList());
    }

    public List<PropertyResponse> searchProperties(String city, String area, PropertyType propertyType,
                                                  Double minRent, Double maxRent, Integer bedrooms,
                                                  FurnishedStatus furnishedStatus) {
        // Sanitize empty strings to null for optional JPQL filters
        String c = (city != null && !city.trim().isEmpty()) ? city.trim() : null;
        String a = (area != null && !area.trim().isEmpty()) ? area.trim() : null;

        List<Property> properties = propertyRepository.searchProperties(
                c, a, propertyType, minRent, maxRent, bedrooms, furnishedStatus, PropertyStatus.AVAILABLE
        );

        return properties.stream()
                .map(this::enrichPropertyResponse)
                .collect(Collectors.toList());
    }

    public PropertyResponse getPropertyById(Long id, Long currentUserId) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));

        PropertyResponse response = enrichPropertyResponse(property);
        if (currentUserId != null) {
            boolean isFav = favoriteRepository.existsByUserIdAndPropertyId(currentUserId, id);
            response.setFavorite(isFav);
        }
        return response;
    }

    public List<PropertyResponse> getPropertiesByOwner(Long ownerId) {
        return propertyRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId)
                .stream()
                .map(this::enrichPropertyResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PropertyResponse createProperty(PropertyRequest request, User owner) {
        if (owner.getRole() != Role.ROLE_LANDLORD && owner.getRole() != Role.ROLE_ADMIN) {
            throw new UnauthorizedException("Only landlords or admins can create properties");
        }

        Property property = new Property();
        mapDtoToEntity(request, property);
        property.setOwner(owner);
        property.setStatus(PropertyStatus.AVAILABLE);
        property.setCreatedAt(LocalDateTime.now());
        property.setUpdatedAt(LocalDateTime.now());

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            boolean first = true;
            for (String url : request.getImageUrls()) {
                PropertyImage img = new PropertyImage(url, "", first, property);
                property.addImage(img);
                first = false;
            }
        }

        Property savedProperty = propertyRepository.save(property);
        return enrichPropertyResponse(savedProperty);
    }

    @Transactional
    public PropertyResponse updateProperty(Long id, PropertyRequest request, User user) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));

        // Check ownership or admin
        if (!property.getOwner().getId().equals(user.getId()) && user.getRole() != Role.ROLE_ADMIN) {
            throw new UnauthorizedException("You are not authorized to update this property");
        }

        mapDtoToEntity(request, property);
        if (request.getStatus() != null) {
            property.setStatus(request.getStatus());
        }
        property.setUpdatedAt(LocalDateTime.now());

        Property updatedProperty = propertyRepository.save(property);
        return enrichPropertyResponse(updatedProperty);
    }

    @Transactional
    public void deleteProperty(Long id, User user) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));

        if (!property.getOwner().getId().equals(user.getId()) && user.getRole() != Role.ROLE_ADMIN) {
            throw new UnauthorizedException("You are not authorized to delete this property");
        }

        // Delete uploaded physical images
        if (property.getImages() != null) {
            for (PropertyImage img : property.getImages()) {
                fileStorageService.deleteFile(img.getImageUrl());
            }
        }

        propertyRepository.delete(property);
    }

    @Transactional
    public List<PropertyImageDto> addImages(Long propertyId, List<MultipartFile> files, User user) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));

        if (!property.getOwner().getId().equals(user.getId()) && user.getRole() != Role.ROLE_ADMIN) {
            throw new UnauthorizedException("You are not authorized to upload images for this property");
        }

        boolean hasPrimary = property.getImages().stream().anyMatch(PropertyImage::isPrimary);
        List<PropertyImage> newImages = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                String fileUrl = fileStorageService.storeFile(file);
                boolean isPrimary = !hasPrimary;
                PropertyImage img = new PropertyImage(fileUrl, file.getOriginalFilename(), isPrimary, property);
                property.addImage(img);
                newImages.add(img);
                hasPrimary = true;
            }
        }

        propertyRepository.save(property);

        return newImages.stream()
                .map(PropertyImageDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteImage(Long propertyId, Long imageId, User user) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));

        if (!property.getOwner().getId().equals(user.getId()) && user.getRole() != Role.ROLE_ADMIN) {
            throw new UnauthorizedException("You are not authorized to delete images from this property");
        }

        PropertyImage image = propertyImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + imageId));

        if (!image.getProperty().getId().equals(propertyId)) {
            throw new BadRequestException("Image does not belong to this property");
        }

        fileStorageService.deleteFile(image.getImageUrl());
        property.removeImage(image);
        propertyRepository.save(property);
    }

    @Transactional
    public PropertyResponse changeStatus(Long propertyId, PropertyStatus status, User user) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));

        if (!property.getOwner().getId().equals(user.getId()) && user.getRole() != Role.ROLE_ADMIN) {
            throw new UnauthorizedException("You are not authorized to update status for this property");
        }

        property.setStatus(status);
        property.setUpdatedAt(LocalDateTime.now());
        Property updated = propertyRepository.save(property);
        return enrichPropertyResponse(updated);
    }

    private void mapDtoToEntity(PropertyRequest request, Property property) {
        property.setTitle(request.getTitle());
        property.setDescription(request.getDescription());
        property.setPropertyType(request.getPropertyType());
        property.setRent(request.getRent());
        property.setSecurityDeposit(request.getSecurityDeposit());
        property.setAddress(request.getAddress());
        property.setCity(request.getCity());
        property.setArea(request.getArea());
        property.setPincode(request.getPincode());
        property.setLatitude(request.getLatitude());
        property.setLongitude(request.getLongitude());
        property.setBedrooms(request.getBedrooms());
        property.setBathrooms(request.getBathrooms());
        property.setSqft(request.getSqft());
        property.setFurnishedStatus(request.getFurnishedStatus());
        property.setAvailableFrom(request.getAvailableFrom());
    }

    private PropertyResponse enrichPropertyResponse(Property property) {
        PropertyResponse response = PropertyResponse.fromEntity(property);
        Double avgRating = reviewRepository.findAverageRatingByPropertyId(property.getId());
        long count = reviewRepository.countByPropertyId(property.getId());
        response.setAverageRating(avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : null);
        response.setTotalReviews(count);
        return response;
    }
}
