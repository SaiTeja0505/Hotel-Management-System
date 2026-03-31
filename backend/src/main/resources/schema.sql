-- Database Schema for Hotel Booking System
-- Database Name: hotel_booking

CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP
);

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(255),
    role_id BIGINT NOT NULL,
    status ENUM('ACTIVE', 'PENDING', 'BLOCKED') NOT NULL,
    last_login TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE owner_approvals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id BIGINT NOT NULL,
    approved_by BIGINT NOT NULL,
    status ENUM('APPROVED', 'REJECTED', 'PENDING') NOT NULL,
    remarks TEXT,
    created_at TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES users(id),
    FOREIGN KEY (approved_by) REFERENCES users(id)
);

CREATE TABLE hotels (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    location VARCHAR(255),
    address VARCHAR(255),
    city VARCHAR(255),
    state VARCHAR(255),
    country VARCHAR(255),
    rating DOUBLE DEFAULT 0,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE hotel_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hotel_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    created_at TIMESTAMP,
    FOREIGN KEY (hotel_id) REFERENCES hotels(id)
);

CREATE TABLE rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hotel_id BIGINT NOT NULL,
    room_number VARCHAR(255) NOT NULL,
    room_type VARCHAR(255),
    price_per_night DOUBLE NOT NULL,
    capacity INT,
    max_guests INT,
    description TEXT,
    total_rooms INT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (hotel_id) REFERENCES hotels(id)
);

CREATE TABLE room_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    created_at TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES rooms(id)
);

CREATE TABLE amenities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP
);

CREATE TABLE room_amenities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL,
    amenity_id BIGINT NOT NULL,
    FOREIGN KEY (room_id) REFERENCES rooms(id),
    FOREIGN KEY (amenity_id) REFERENCES amenities(id)
);

CREATE TABLE bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_reference VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    total_price DOUBLE NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'REJECTED') NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (room_id) REFERENCES rooms(id)
);

CREATE TABLE booking_status_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'REJECTED') NOT NULL,
    changed_by BIGINT NOT NULL,
    created_at TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(id),
    FOREIGN KEY (changed_by) REFERENCES users(id)
);

-- Indexes for performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role_id ON users(role_id);
CREATE INDEX idx_hotels_owner_id ON hotels(owner_id);
CREATE INDEX idx_hotels_status ON hotels(status);
CREATE INDEX idx_rooms_hotel_id ON rooms(hotel_id);
CREATE INDEX idx_bookings_user_id ON bookings(user_id);
CREATE INDEX idx_bookings_room_id ON bookings(room_id);
CREATE INDEX idx_bookings_status ON bookings(status);