package com.foodapp.foodhub.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompleteRestaurantDataRequest
{
    @NotBlank(message = "Description is required.")
    private String description;

    @NotBlank(message = "Address is required.")
    private String address;

    private String imageUrl;

    private Long subcategoryId;

    private Set<Long> zoneIds;
}
