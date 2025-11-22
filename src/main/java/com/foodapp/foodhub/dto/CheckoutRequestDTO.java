package com.foodapp.foodhub.dto;

import lombok.Data;

@Data
public class CheckoutRequestDTO {
    private Long userId;
    private Long RestaurantId;
    private double userLatitude;
    private double userLongitude;
    private String address;
    private String phone;
    private String notes;
    private String area;
    private String street;
    private String building;
    private String floor;
    private String apartment;
}
