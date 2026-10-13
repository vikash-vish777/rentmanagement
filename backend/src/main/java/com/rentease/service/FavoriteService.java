package com.rentease.service;

import com.rentease.dto.PropertyResponse;
import com.rentease.entity.Favorite;
import com.rentease.entity.Property;
import com.rentease.entity.User;
import com.rentease.exception.ResourceNotFoundException;
import com.rentease.repository.FavoriteRepository;
import com.rentease.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Transactional
    public void addFavorite(Long propertyId, User user) {
        if (!favoriteRepository.existsByUserIdAndPropertyId(user.getId(), propertyId)) {
            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));
            Favorite favorite = new Favorite(user, property);
            favoriteRepository.save(favorite);
        }
    }

    @Transactional
    public void removeFavorite(Long propertyId, User user) {
        favoriteRepository.deleteByUserIdAndPropertyId(user.getId(), propertyId);
    }

    public List<PropertyResponse> getUserFavorites(Long userId) {
        return favoriteRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(fav -> {
                    PropertyResponse res = PropertyResponse.fromEntity(fav.getProperty());
                    res.setFavorite(true);
                    return res;
                })
                .collect(Collectors.toList());
    }

    public boolean isFavorite(Long propertyId, Long userId) {
        return favoriteRepository.existsByUserIdAndPropertyId(userId, propertyId);
    }
}
