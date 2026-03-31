package com.hotel.hotel_management.service;

import com.hotel.hotel_management.dto.JwtResponse;
import com.hotel.hotel_management.dto.LoginRequest;
import com.hotel.hotel_management.dto.RegisterRequest;
import com.hotel.hotel_management.dto.ApiResponse;
import com.hotel.hotel_management.model.Role;
import com.hotel.hotel_management.model.User;
import com.hotel.hotel_management.model.OwnerApproval;
import com.hotel.hotel_management.repository.RoleRepository;
import com.hotel.hotel_management.repository.UserRepository;
import com.hotel.hotel_management.repository.OwnerApprovalRepository;
import com.hotel.hotel_management.security.JwtUtils;
import com.hotel.hotel_management.security.UserDetailsImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OwnerApprovalRepository ownerApprovalRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       OwnerApprovalRepository ownerApprovalRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.ownerApprovalRepository = ownerApprovalRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    public ApiResponse registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Email already exists");
        }

        Role role = roleRepository.findByName("USER").orElseThrow(() -> new IllegalStateException("Role USER not found"));

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(role);
        user.setStatus(User.Status.ACTIVE);

        userRepository.save(user);
        return new ApiResponse("User registered successfully");
    }

    public ApiResponse registerOwner(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Email already exists");
        }

        Role role = roleRepository.findByName("HOTEL_OWNER").orElseThrow(() -> new IllegalStateException("Role HOTEL_OWNER not found"));

        User owner = new User();
        owner.setName(request.getName());
        owner.setEmail(request.getEmail());
        owner.setPassword(passwordEncoder.encode(request.getPassword()));
        owner.setPhone(request.getPhone());
        owner.setRole(role);
        owner.setStatus(User.Status.PENDING);

        owner = userRepository.save(owner);

        OwnerApproval ownerApproval = new OwnerApproval();
        ownerApproval.setOwner(owner);
        ownerApproval.setStatus(OwnerApproval.Status.PENDING);

        ownerApprovalRepository.save(ownerApproval);
        return new ApiResponse("Owner registered successfully and pending approval");
    }

    public JwtResponse authenticateUser(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        return new JwtResponse(jwt, "Bearer", user.getId(), user.getName(), user.getEmail(), user.getRole().getName());
    }
}
