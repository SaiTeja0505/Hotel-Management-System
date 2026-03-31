package com.hotel.hotel_management.repository;

import com.hotel.hotel_management.model.Booking;
import com.hotel.hotel_management.model.Room;
import com.hotel.hotel_management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);
    List<Booking> findByRoom(Room room);
    List<Booking> findByStatus(Booking.Status status);
    long countByRoomAndStatus(Room room, Booking.Status status);
}
