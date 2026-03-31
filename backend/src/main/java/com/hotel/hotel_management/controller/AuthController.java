package com.hotel.hotel_management.controller;

import com.hotel.hotel_management.dto.ApiResponse;
import com.hotel.hotel_management.dto.JwtResponse;
import com.hotel.hotel_management.dto.LoginRequest;
import com.hotel.hotel_management.dto.RegisterRequest;
import com.hotel.hotel_management.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register/user")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.registerUser(request));
    }

    @PostMapping("/register/owner")
    public ResponseEntity<ApiResponse> registerOwner(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.registerOwner(request));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.authenticateUser(request));
    }
}
