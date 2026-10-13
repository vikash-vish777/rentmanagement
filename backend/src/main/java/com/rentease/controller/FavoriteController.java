package com.rentease.controller;

import com.rentease.dto.ApiResponse;
import com.rentease.dto.PropertyResponse;
import com.rentease.entity.User;
import com.rentease.service.AuthService;
import com.rentease.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<List<PropertyResponse>> getFavorites(Authentication authentication) {
        User user = authService.getCurrentUserEntity(authentication.getName());
        return ResponseEntity.ok(favoriteService.getUserFavorites(user.getId()));
    }

    @PostMapping("/{propertyId}")
    public ResponseEntity<ApiResponse> addFavorite(
            @PathVariable Long propertyId,
            Authentication authentication
    ) {
        User user = authService.getCurrentUserEntity(authentication.getName());
        favoriteService.addFavorite(propertyId, user);
        return ResponseEntity.ok(new ApiResponse(true, "Property added to favorites"));
    }

    @DeleteMapping("/{propertyId}")
    public ResponseEntity<ApiResponse> removeFavorite(
            @PathVariable Long propertyId,
            Authentication authentication
    ) {
        User user = authService.getCurrentUserEntity(authentication.getName());
        favoriteService.removeFavorite(propertyId, user);
        return ResponseEntity.ok(new ApiResponse(true, "Property removed from favorites"));
    }
}
