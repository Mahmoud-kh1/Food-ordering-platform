package com.foodapp.foodhub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantApplicationDto
{
    private Long id;
    private String restaurantName;
    private String status;
    private String rejectionReason;
    private String reviewedBy;
}
