package com.hotel.hotel_management.service;

import com.hotel.hotel_management.dto.BookingRequest;
import com.hotel.hotel_management.dto.BookingResponse;
import com.hotel.hotel_management.exception.ResourceNotFoundException;
import com.hotel.hotel_management.model.Booking;
import com.hotel.hotel_management.model.BookingStatusHistory;
import com.hotel.hotel_management.model.Room;
import com.hotel.hotel_management.model.User;
import com.hotel.hotel_management.repository.BookingRepository;
import com.hotel.hotel_management.repository.BookingStatusHistoryRepository;
import com.hotel.hotel_management.repository.RoomRepository;
import com.hotel.hotel_management.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingStatusHistoryRepository bookingStatusHistoryRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository,
                          BookingStatusHistoryRepository bookingStatusHistoryRepository,
                          RoomRepository roomRepository,
                          UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.bookingStatusHistoryRepository = bookingStatusHistoryRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BookingResponse createBooking(Long userId, BookingRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Room room = roomRepository.findById(request.getRoomId()).orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        if (request.getCheckOutDate().isBefore(request.getCheckInDate()) || request.getCheckOutDate().isEqual(request.getCheckInDate())) {
            throw new IllegalStateException("Invalid check-in/check-out dates");
        }

        long days = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        BigDecimal totalPrice = room.getPrice().multiply(BigDecimal.valueOf(days));

        // Check availability - for simplicity, assume unlimited rooms per type
        // In a real system, you'd have inventory management

        Booking booking = new Booking();
        booking.setBookingReference("BK-" + UUID.randomUUID().toString());
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(request.getCheckInDate());
        booking.setCheckOutDate(request.getCheckOutDate());
        booking.setTotalPrice(totalPrice);
        booking.setStatus(Booking.Status.PENDING);

        Booking saved = bookingRepository.save(booking);

        BookingStatusHistory history = new BookingStatusHistory();
        history.setBooking(saved);
        history.setStatus(saved.getStatus());
        history.setChangedBy(user);
        bookingStatusHistoryRepository.save(history);

        return toDto(saved);
    }

    public List<BookingResponse> getBookingsByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return bookingRepository.findByUser(user).stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<BookingResponse> getBookingsByOwner(Long ownerId) {
        userRepository.findById(ownerId).orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
        return bookingRepository.findAll().stream().filter(b -> b.getRoom().getHotel().getOwner().getId().equals(ownerId)).map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public BookingResponse acceptBooking(Long ownerId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (!booking.getRoom().getHotel().getOwner().getId().equals(ownerId)) {
            throw new IllegalStateException("Not your booking");
        }

        if (booking.getStatus() != Booking.Status.PENDING) {
            throw new IllegalStateException("Booking cannot be accepted");
        }

        booking.setStatus(Booking.Status.CONFIRMED);
        Booking updated = bookingRepository.save(booking);

        BookingStatusHistory history = new BookingStatusHistory();
        history.setBooking(updated);
        history.setStatus(updated.getStatus());
        history.setChangedBy(userRepository.getReferenceById(ownerId));
        bookingStatusHistoryRepository.save(history);

        return toDto(updated);
    }

    @Transactional
    public BookingResponse rejectBooking(Long ownerId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (!booking.getRoom().getHotel().getOwner().getId().equals(ownerId)) {
            throw new IllegalStateException("Not your booking");
        }

        if (booking.getStatus() != Booking.Status.PENDING) {
            throw new IllegalStateException("Booking cannot be rejected");
        }

        booking.setStatus(Booking.Status.REJECTED);
        Booking updated = bookingRepository.save(booking);

        BookingStatusHistory history = new BookingStatusHistory();
        history.setBooking(updated);
        history.setStatus(updated.getStatus());
        history.setChangedBy(userRepository.getReferenceById(ownerId));
        bookingStatusHistoryRepository.save(history);

        return toDto(updated);
    }

    @Transactional
    public BookingResponse cancelBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (!booking.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Not your booking");
        }

        if (booking.getStatus() != Booking.Status.PENDING && booking.getStatus() != Booking.Status.CONFIRMED) {
            throw new IllegalStateException("Booking cannot be cancelled");
        }

        booking.setStatus(Booking.Status.CANCELLED);
        Booking updated = bookingRepository.save(booking);

        BookingStatusHistory history = new BookingStatusHistory();
        history.setBooking(updated);
        history.setStatus(updated.getStatus());
        history.setChangedBy(userRepository.getReferenceById(userId));
        bookingStatusHistoryRepository.save(history);

        return toDto(updated);
    }

    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    private BookingResponse toDto(Booking booking) {
        return new BookingResponse(booking.getId(), booking.getBookingReference(), booking.getUser().getId(), booking.getRoom().getId(), booking.getCheckInDate(), booking.getCheckOutDate(), booking.getTotalPrice(), booking.getStatus().name());
    }
}
