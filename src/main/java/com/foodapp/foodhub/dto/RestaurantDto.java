package com.foodapp.foodhub.dto;

import com.foodapp.foodhub.enums.RestaurantStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantDto
{
    public Long id;
    public String name;
    public String phone;
    public String imageUrl;
    public String address;
    public Double rating;
    public Long subcategoryId;
    public String subcategoryName;
    public Long categoryId;
    public String categoryName;
    public RestaurantStatus status;
}
