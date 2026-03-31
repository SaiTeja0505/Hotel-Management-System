package com.hotel.hotel_management.controller;

import com.hotel.hotel_management.dto.ApiResponse;
import com.hotel.hotel_management.dto.BookingResponse;
import com.hotel.hotel_management.dto.HotelRequest;
import com.hotel.hotel_management.dto.HotelResponse;
import com.hotel.hotel_management.dto.RoomRequest;
import com.hotel.hotel_management.dto.RoomResponse;
import com.hotel.hotel_management.model.User;
import com.hotel.hotel_management.repository.UserRepository;
import com.hotel.hotel_management.security.SecurityUtils;
import com.hotel.hotel_management.service.BookingService;
import com.hotel.hotel_management.service.HotelService;
import com.hotel.hotel_management.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/owner")
public class OwnerController {

    private final HotelService hotelService;
    private final RoomService roomService;
    private final BookingService bookingService;
    private final UserRepository userRepository;

    public OwnerController(HotelService hotelService, RoomService roomService, BookingService bookingService, UserRepository userRepository) {
        this.hotelService = hotelService;
        this.roomService = roomService;
        this.bookingService = bookingService;
        this.userRepository = userRepository;
    }

    private User currentUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        return userRepository.findByEmail(email).orElseThrow();
    }

    @PostMapping("/hotels")
    public ResponseEntity<HotelResponse> createHotel(@Valid @RequestBody HotelRequest request) {
        return ResponseEntity.ok(hotelService.createHotel(currentUser().getId(), request));
    }

    @PutMapping("/hotels/{id}")
    public ResponseEntity<HotelResponse> updateHotel(@PathVariable Long id, @Valid @RequestBody HotelRequest request) {
        return ResponseEntity.ok(hotelService.updateHotel(currentUser().getId(), id, request));
    }

    @DeleteMapping("/hotels/{id}")
    public ResponseEntity<ApiResponse> deleteHotel(@PathVariable Long id) {
        hotelService.deleteHotel(currentUser().getId(), id);
        return ResponseEntity.ok(new ApiResponse("Hotel deleted"));
    }

    @PostMapping("/rooms")
    public ResponseEntity<RoomResponse> createRoom(@Valid @RequestBody RoomRequest request) {
        return ResponseEntity.ok(roomService.createRoom(currentUser().getId(), request));
    }

    @PutMapping("/rooms/{id}")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable Long id, @Valid @RequestBody RoomRequest request) {
        return ResponseEntity.ok(roomService.updateRoom(currentUser().getId(), id, request));
    }

    @DeleteMapping("/rooms/{id}")
    public ResponseEntity<ApiResponse> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(currentUser().getId(), id);
        return ResponseEntity.ok(new ApiResponse("Room deleted"));
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingResponse>> getBookings() {
        return ResponseEntity.ok(bookingService.getBookingsByOwner(currentUser().getId()));
    }

    @PutMapping("/bookings/{id}/accept")
    public ResponseEntity<ApiResponse> acceptBooking(@PathVariable Long id) {
        bookingService.acceptBooking(currentUser().getId(), id);
        return ResponseEntity.ok(new ApiResponse("Booking accepted"));
    }

    @PutMapping("/bookings/{id}/reject")
    public ResponseEntity<ApiResponse> rejectBooking(@PathVariable Long id) {
        bookingService.rejectBooking(currentUser().getId(), id);
        return ResponseEntity.ok(new ApiResponse("Booking rejected"));
    }
}
