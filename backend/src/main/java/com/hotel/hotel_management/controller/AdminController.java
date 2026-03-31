package com.hotel.hotel_management.controller;

import com.hotel.hotel_management.dto.ApiResponse;
import com.hotel.hotel_management.model.Booking;
import com.hotel.hotel_management.model.Hotel;
import com.hotel.hotel_management.model.OwnerApproval;
import com.hotel.hotel_management.model.User;
import com.hotel.hotel_management.repository.UserRepository;
import com.hotel.hotel_management.security.SecurityUtils;
import com.hotel.hotel_management.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository;

    public AdminController(AdminService adminService, UserRepository userRepository) {
        this.adminService = adminService;
        this.userRepository = userRepository;
    }

    private Long adminId() {
        String email = SecurityUtils.getCurrentUserEmail();
        return userRepository.findByEmail(email).orElseThrow().getId();
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PutMapping("/users/{id}/block")
    public ResponseEntity<ApiResponse> blockUser(@PathVariable Long id) {
        adminService.blockUser(id);
        return ResponseEntity.ok(new ApiResponse("User blocked"));
    }

    @GetMapping("/owners/pending")
    public ResponseEntity<List<OwnerApproval>> getPendingOwners() {
        return ResponseEntity.ok(adminService.getPendingOwners());
    }

    @PutMapping("/owners/{id}/approve")
    public ResponseEntity<ApiResponse> approveOwner(@PathVariable Long id) {
        adminService.approveOwner(id, adminId());
        return ResponseEntity.ok(new ApiResponse("Owner approved"));
    }

    @PutMapping("/owners/{id}/reject")
    public ResponseEntity<ApiResponse> rejectOwner(@PathVariable Long id) {
        adminService.rejectOwner(id, adminId());
        return ResponseEntity.ok(new ApiResponse("Owner rejected"));
    }

    @GetMapping("/hotels")
    public ResponseEntity<List<Hotel>> getHotels() {
        return ResponseEntity.ok(adminService.getAllHotels());
    }

    @PutMapping("/hotels/{id}/approve")
    public ResponseEntity<ApiResponse> approveHotel(@PathVariable Long id) {
        adminService.approveHotel(id);
        return ResponseEntity.ok(new ApiResponse("Hotel approved"));
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getBookings() {
        return ResponseEntity.ok(adminService.getAllBookings());
    }
}
