package com.hotel.hotel_management.controller;

import com.hotel.hotel_management.dto.ApiResponse;
import com.hotel.hotel_management.dto.BookingRequest;
import com.hotel.hotel_management.dto.BookingResponse;
import com.hotel.hotel_management.dto.HotelResponse;
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
public class UserController {

    private final HotelService hotelService;
    private final RoomService roomService;
    private final BookingService bookingService;
    private final UserRepository userRepository;

    public UserController(HotelService hotelService, RoomService roomService, BookingService bookingService, UserRepository userRepository) {
        this.hotelService = hotelService;
        this.roomService = roomService;
        this.bookingService = bookingService;
        this.userRepository = userRepository;
    }

    @GetMapping("/hotels")
    public ResponseEntity<List<HotelResponse>> getAllHotels() {
        return ResponseEntity.ok(hotelService.getAllHotels());
    }

    @GetMapping("/hotels/{id}")
    public ResponseEntity<HotelResponse> getHotelById(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.getHotelById(id));
    }

    @GetMapping("/rooms/{hotelId}")
    public ResponseEntity<List<RoomResponse>> getRoomsByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(roomService.getRoomsByHotel(hotelId));
    }

    @PostMapping("/bookings")
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmail(email).orElseThrow();
        return ResponseEntity.ok(bookingService.createBooking(user.getId(), request));
    }

    @GetMapping("/bookings/my")
    public ResponseEntity<List<BookingResponse>> getMyBookings() {
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmail(email).orElseThrow();
        return ResponseEntity.ok(bookingService.getBookingsByUser(user.getId()));
    }

    @PutMapping("/bookings/{id}/cancel")
    public ResponseEntity<ApiResponse> cancelBooking(@PathVariable Long id) {
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmail(email).orElseThrow();
        bookingService.cancelBooking(user.getId(), id);
        return ResponseEntity.ok(new ApiResponse("Booking cancelled"));
    }
}
