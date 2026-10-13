package com.rentease.service;

import com.rentease.dto.JwtResponse;
import com.rentease.dto.LoginRequest;
import com.rentease.dto.RegisterRequest;
import com.rentease.dto.UserDto;
import com.rentease.entity.Role;
import com.rentease.entity.User;
import com.rentease.exception.BadRequestException;
import com.rentease.exception.ResourceNotFoundException;
import com.rentease.repository.UserRepository;
import com.rentease.security.JwtUtils;
import com.rentease.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Transactional
    public UserDto registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Error: Email is already in use!");
        }

        Role role = registerRequest.getRole();
        if (role == null || role == Role.ROLE_ADMIN) {
            role = Role.ROLE_TENANT;
        }

        User user = new User(
                registerRequest.getName(),
                registerRequest.getEmail(),
                registerRequest.getPhone(),
                passwordEncoder.encode(registerRequest.getPassword()),
                role
        );

        User savedUser = userRepository.save(user);
        return UserDto.fromEntity(savedUser);
    }

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        return new JwtResponse(
                jwt,
                userDetails.getId(),
                userDetails.getName(),
                userDetails.getUsername(),
                userDetails.getPhone(),
                role
        );
    }

    public UserDto getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return UserDto.fromEntity(user);
    }

    public User getCurrentUserEntity(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}
