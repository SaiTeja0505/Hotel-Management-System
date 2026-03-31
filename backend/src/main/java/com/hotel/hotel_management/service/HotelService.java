package com.hotel.hotel_management.service;

import com.hotel.hotel_management.dto.HotelRequest;
import com.hotel.hotel_management.dto.HotelResponse;
import com.hotel.hotel_management.exception.ResourceNotFoundException;
import com.hotel.hotel_management.model.Hotel;
import com.hotel.hotel_management.model.User;
import com.hotel.hotel_management.repository.HotelRepository;
import com.hotel.hotel_management.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;

    public HotelService(HotelRepository hotelRepository, UserRepository userRepository) {
        this.hotelRepository = hotelRepository;
        this.userRepository = userRepository;
    }

    public List<HotelResponse> getAllHotels() {
        return hotelRepository.findByStatus(Hotel.Status.APPROVED).stream().map(this::toDto).collect(Collectors.toList());
    }

    public HotelResponse getHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
        return toDto(hotel);
    }

    @Transactional
    public HotelResponse createHotel(Long ownerId, HotelRequest request) {
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
        Hotel hotel = new Hotel();
        hotel.setOwner(owner);
        hotel.setName(request.getName());
        hotel.setDescription(request.getDescription());
        hotel.setLocation(request.getLocation());
        hotel.setAddress(request.getAddress());
        hotel.setCity(request.getCity());
        hotel.setState(request.getState());
        hotel.setCountry(request.getCountry());
        hotel.setStatus(Hotel.Status.PENDING);

        return toDto(hotelRepository.save(hotel));
    }

    @Transactional
    public HotelResponse updateHotel(Long ownerId, Long hotelId, HotelRequest request) {
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
        if (!hotel.getOwner().getId().equals(ownerId)) {
            throw new IllegalStateException("Not your hotel");
        }

        hotel.setName(request.getName());
        hotel.setDescription(request.getDescription());
        hotel.setLocation(request.getLocation());
        hotel.setAddress(request.getAddress());
        hotel.setCity(request.getCity());
        hotel.setState(request.getState());
        hotel.setCountry(request.getCountry());
        hotel.setStatus(Hotel.Status.PENDING);

        return toDto(hotelRepository.save(hotel));
    }

    public void deleteHotel(Long ownerId, Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
        if (!hotel.getOwner().getId().equals(ownerId)) {
            throw new IllegalStateException("Not your hotel");
        }
        hotelRepository.delete(hotel);
    }

    public HotelResponse approveHotel(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
        hotel.setStatus(Hotel.Status.APPROVED);
        return toDto(hotelRepository.save(hotel));
    }

    private HotelResponse toDto(Hotel hotel) {
        return new HotelResponse(hotel.getId(), hotel.getName(), hotel.getDescription(), hotel.getLocation(), hotel.getAddress(), hotel.getCity(), hotel.getState(), hotel.getCountry(), hotel.getStatus().name(), hotel.getOwner().getId());
    }
}
