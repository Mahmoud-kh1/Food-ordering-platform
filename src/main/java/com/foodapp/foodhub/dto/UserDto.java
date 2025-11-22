package com.foodapp.foodhub.dto;

import lombok.Data;

import java.util.Set;
@Data
public class UserDto
{
    private String fullName;
    private String email;
    private String phone;
    private Set<RestaurantDto> restaurants;
}
