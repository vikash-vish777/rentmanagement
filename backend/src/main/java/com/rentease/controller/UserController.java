package com.rentease.controller;

import com.rentease.dto.ApiResponse;
import com.rentease.dto.UserDto;
import com.rentease.entity.User;
import com.rentease.exception.ResourceNotFoundException;
import com.rentease.repository.UserRepository;
import com.rentease.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return ResponseEntity.ok(UserDto.fromEntity(user));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse> updateProfile(
            @RequestBody Map<String, String> payload,
            Authentication authentication
    ) {
        User user = authService.getCurrentUserEntity(authentication.getName());
        if (payload.containsKey("name") && !payload.get("name").trim().isEmpty()) {
            user.setName(payload.get("name").trim());
        }
        if (payload.containsKey("phone") && !payload.get("phone").trim().isEmpty()) {
            user.setPhone(payload.get("phone").trim());
        }
        userRepository.save(user);
        return ResponseEntity.ok(new ApiResponse(true, "Profile updated successfully", UserDto.fromEntity(user)));
    }
}
