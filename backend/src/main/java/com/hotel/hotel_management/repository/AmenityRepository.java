package com.hotel.hotel_management.repository;

import com.hotel.hotel_management.model.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmenityRepository extends JpaRepository<Amenity, Long> {
}
