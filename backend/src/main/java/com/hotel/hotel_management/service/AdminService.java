package com.hotel.hotel_management.service;

import com.hotel.hotel_management.model.Booking;
import com.hotel.hotel_management.model.Hotel;
import com.hotel.hotel_management.model.OwnerApproval;
import com.hotel.hotel_management.model.User;
import com.hotel.hotel_management.repository.BookingRepository;
import com.hotel.hotel_management.repository.HotelRepository;
import com.hotel.hotel_management.repository.OwnerApprovalRepository;
import com.hotel.hotel_management.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final OwnerApprovalRepository ownerApprovalRepository;
    private final BookingRepository bookingRepository;

    public AdminService(UserRepository userRepository,
                        HotelRepository hotelRepository,
                        OwnerApprovalRepository ownerApprovalRepository,
                        BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.hotelRepository = hotelRepository;
        this.ownerApprovalRepository = ownerApprovalRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void blockUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setStatus(User.Status.BLOCKED);
        userRepository.save(user);
    }

    public List<OwnerApproval> getPendingOwners() {
        return ownerApprovalRepository.findByStatus(OwnerApproval.Status.PENDING);
    }

    @Transactional
    public void approveOwner(Long id, Long adminId) {
        OwnerApproval approval = ownerApprovalRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Owner approval not found"));
        User admin = userRepository.findById(adminId).orElseThrow(() -> new IllegalArgumentException("Admin not found"));
        approval.setStatus(OwnerApproval.Status.APPROVED);
        approval.setApprovedBy(admin);
        approval.getOwner().setStatus(User.Status.ACTIVE);
        ownerApprovalRepository.save(approval);
        userRepository.save(approval.getOwner());
    }

    @Transactional
    public void rejectOwner(Long id, Long adminId) {
        OwnerApproval approval = ownerApprovalRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Owner approval not found"));
        User admin = userRepository.findById(adminId).orElseThrow(() -> new IllegalArgumentException("Admin not found"));
        approval.setStatus(OwnerApproval.Status.REJECTED);
        approval.setApprovedBy(admin);
        approval.getOwner().setStatus(User.Status.BLOCKED);
        ownerApprovalRepository.save(approval);
        userRepository.save(approval.getOwner());
    }

    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    @Transactional
    public void approveHotel(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new IllegalArgumentException("Hotel not found"));
        hotel.setStatus(Hotel.Status.APPROVED);
        hotelRepository.save(hotel);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
}
