package com.construction.cms.controller;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.construction.cms.model.Role;
import com.construction.cms.model.User;
import com.construction.cms.payload.request.LoginRequest;
import com.construction.cms.payload.request.SignupRequest;
import com.construction.cms.payload.request.PasswordChangeRequest;
import com.construction.cms.payload.response.JwtResponse;
import com.construction.cms.payload.response.MessageResponse;
import com.construction.cms.repository.UserRepository;
import com.construction.cms.security.jwt.JwtUtils;
import com.construction.cms.security.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();    
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt, 
                                                 userDetails.getId(), 
                                                 userDetails.getUsername(), 
                                                 userDetails.getEmail(), 
                                                 roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        // if (userRepository.existsByUsername(signUpRequest.getUsername())) {
        //      return ResponseEntity
        //          .badRequest()
        //          .body(new MessageResponse("Error: Username is already taken!"));
        // }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Default role is ENGINEER if not specified, or ADMIN if requested.
        // For simplicity we'll just take the string from request or default.
        String roleStr = (signUpRequest.getRole() != null && !signUpRequest.getRole().isEmpty()) 
                        ? signUpRequest.getRole().iterator().next() : "engineer";
        
        Role role;
        try {
             role = Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
             role = Role.ENGINEER;
        }

        User user = new User(signUpRequest.getUsername(), 
                             signUpRequest.getEmail(),
                             encoder.encode(signUpRequest.getPassword()),
                             role);
        user.setPhone(signUpRequest.getPhone());
        user.setAddress(signUpRequest.getAddress());
        user.setSalary(signUpRequest.getSalary());
        user.setContractType(signUpRequest.getContractType());
        user.setStartDate(signUpRequest.getStartDate());
        user.setEndDate(signUpRequest.getEndDate());

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
    @GetMapping("/reset-admin")
    public ResponseEntity<?> resetAdminPassword() {
        User admin = userRepository.findByEmail("admin@cms.com")
            .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        String newPassword = encoder.encode("admin123");
        admin.setPassword(newPassword);
        admin.setActive(true);
        userRepository.save(admin);
        
        return ResponseEntity.ok("Admin password reset to: admin123");
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(new MessageResponse("Unauthorized"));
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Current password is incorrect"));
        }

        user.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Password updated successfully"));
    }
}
