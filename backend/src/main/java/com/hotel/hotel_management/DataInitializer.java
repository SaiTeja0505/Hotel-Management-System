package com.hotel.hotel_management;

import com.hotel.hotel_management.model.Role;
import com.hotel.hotel_management.model.User;
import com.hotel.hotel_management.repository.RoleRepository;
import com.hotel.hotel_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;
    @Value("${app.admin.password}")
    private String adminPassword;
    @Value("${app.admin.name}")
    private String adminName;
    @Value("${app.admin.phone}")
    private String adminPhone;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            Role admin = new Role();
            admin.setName("ADMIN");
            roleRepository.save(admin);

            Role owner = new Role();
            owner.setName("HOTEL_OWNER");
            roleRepository.save(owner);

            Role user = new Role();
            user.setName("USER");
            roleRepository.save(user);
        }

        if (!userRepository.existsByEmail(adminEmail)) {
            Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();
            User admin = new User();
            admin.setName(adminName);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setPhone(adminPhone);
            admin.setRole(adminRole);
            admin.setStatus(User.Status.ACTIVE);
            userRepository.save(admin);
        }
    }
}
