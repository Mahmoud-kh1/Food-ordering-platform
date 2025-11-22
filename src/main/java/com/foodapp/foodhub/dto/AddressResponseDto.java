package com.foodapp.foodhub.dto;

import lombok.*;

@Getter
@Setter
public class AddressResponseDto {
    private Long id;
    private String street;
    private String building;
    private String floor;
    private String apartment;
    private String area;
    private String notes;
    private String phone;
    private Double latitude;
    private Double longitude;
}
