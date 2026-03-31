package com.hotel.hotel_management.dto;

public class HotelResponse {
    private Long id;
    private String name;
    private String description;
    private String location;
    private String address;
    private String city;
    private String state;
    private String country;
    private String status;
    private Long ownerId;

    public HotelResponse() {}

    public HotelResponse(Long id, String name, String description, String location, String address, String city, String state, String country, String status, Long ownerId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.status = status;
        this.ownerId = ownerId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}
