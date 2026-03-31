package com.hotel.hotel_management.repository;

import com.hotel.hotel_management.model.Hotel;
import com.hotel.hotel_management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
    List<Hotel> findByStatus(Hotel.Status status);
    List<Hotel> findByOwner(User owner);
}
