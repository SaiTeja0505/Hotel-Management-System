package com.hotel.hotel_management.dto;

import jakarta.validation.constraints.NotBlank;

public class HotelRequest {
    @NotBlank
    private String name;
    private String description;
    @NotBlank
    private String location;
    private String address;
    private String city;
    private String state;
    private String country;

    public HotelRequest() {}

    public HotelRequest(String name, String description, String location, String address, String city, String state, String country) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
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
}
