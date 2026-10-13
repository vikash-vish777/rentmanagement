package com.rentease.controller;

import com.rentease.dto.ApiResponse;
import com.rentease.dto.JwtResponse;
import com.rentease.dto.LoginRequest;
import com.rentease.dto.RegisterRequest;
import com.rentease.dto.UserDto;
import com.rentease.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        UserDto registeredUser = authService.registerUser(registerRequest);
        return ResponseEntity.ok(new ApiResponse(true, "User registered successfully!", registeredUser));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "User not authenticated"));
        }
        UserDto user = authService.getCurrentUser(authentication.getName());
        return ResponseEntity.ok(new ApiResponse(true, "Profile retrieved successfully", user));
    }
}
