package com.hotel.hotel_management.repository;

import com.hotel.hotel_management.model.RoomAmenity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomAmenityRepository extends JpaRepository<RoomAmenity, Long> {
    void deleteByRoomId(Long roomId);
    List<RoomAmenity> findByRoomId(Long roomId);
}
