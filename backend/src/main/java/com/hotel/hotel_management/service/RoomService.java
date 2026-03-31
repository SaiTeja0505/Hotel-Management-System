package com.hotel.hotel_management.service;

import com.hotel.hotel_management.dto.RoomRequest;
import com.hotel.hotel_management.dto.RoomResponse;
import com.hotel.hotel_management.exception.ResourceNotFoundException;
import com.hotel.hotel_management.model.Amenity;
import com.hotel.hotel_management.model.Hotel;
import com.hotel.hotel_management.model.Room;
import com.hotel.hotel_management.model.RoomAmenity;
import com.hotel.hotel_management.repository.AmenityRepository;
import com.hotel.hotel_management.repository.HotelRepository;
import com.hotel.hotel_management.repository.RoomAmenityRepository;
import com.hotel.hotel_management.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final AmenityRepository amenityRepository;
    private final RoomAmenityRepository roomAmenityRepository;

    public RoomService(RoomRepository roomRepository, HotelRepository hotelRepository, AmenityRepository amenityRepository, RoomAmenityRepository roomAmenityRepository) {
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
        this.amenityRepository = amenityRepository;
        this.roomAmenityRepository = roomAmenityRepository;
    }

    public List<RoomResponse> getRoomsByHotel(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
        return roomRepository.findByHotel(hotel).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public RoomResponse createRoom(Long ownerId, RoomRequest request) {
        Hotel hotel = hotelRepository.findById(request.getHotelId()).orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
        if (!hotel.getOwner().getId().equals(ownerId)) {
            throw new IllegalStateException("Not your hotel");
        }
        if (hotel.getStatus() != Hotel.Status.APPROVED) {
            throw new IllegalStateException("Hotel is not approved");
        }

        Room room = new Room();
        room.setHotel(hotel);
        room.setRoomType(request.getRoomType());
        room.setPrice(request.getPrice());
        room.setCapacity(request.getCapacity());
        room.setDescription(request.getDescription());

        // Handle amenities if provided
        if (request.getAmenityIds() != null && !request.getAmenityIds().isEmpty()) {
            List<Amenity> amenities = amenityRepository.findAllById(request.getAmenityIds());
            for (Amenity amenity : amenities) {
                RoomAmenity roomAmenity = new RoomAmenity();
                roomAmenity.setRoom(room);
                roomAmenity.setAmenity(amenity);
                roomAmenityRepository.save(roomAmenity);
            }
        }

        return toDto(roomRepository.save(room));
    }

    @Transactional
    public RoomResponse updateRoom(Long ownerId, Long roomId, RoomRequest request) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        if (!room.getHotel().getOwner().getId().equals(ownerId)) {
            throw new IllegalStateException("Not your hotel");
        }

        room.setRoomType(request.getRoomType());
        room.setPrice(request.getPrice());
        room.setCapacity(request.getCapacity());
        room.setDescription(request.getDescription());

        // Update amenities
        roomAmenityRepository.deleteByRoomId(roomId);
        if (request.getAmenityIds() != null && !request.getAmenityIds().isEmpty()) {
            List<Amenity> amenities = amenityRepository.findAllById(request.getAmenityIds());
            for (Amenity amenity : amenities) {
                RoomAmenity roomAmenity = new RoomAmenity();
                roomAmenity.setRoom(room);
                roomAmenity.setAmenity(amenity);
                roomAmenityRepository.save(roomAmenity);
            }
        }

        return toDto(roomRepository.save(room));
    }

    public void deleteRoom(Long ownerId, Long roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        if (!room.getHotel().getOwner().getId().equals(ownerId)) {
            throw new IllegalStateException("Not your hotel");
        }
        roomRepository.delete(room);
    }

    private RoomResponse toDto(Room room) {
        List<String> amenities = roomAmenityRepository.findByRoomId(room.getId())
                .stream()
                .map(ra -> ra.getAmenity().getName())
                .collect(Collectors.toList());

        return new RoomResponse(room.getId(), room.getRoomType(), room.getPrice(), room.getCapacity(), room.getDescription(), amenities);
    }
}
