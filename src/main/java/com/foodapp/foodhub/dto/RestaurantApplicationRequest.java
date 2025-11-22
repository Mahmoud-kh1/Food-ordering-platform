package com.foodapp.foodhub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RestaurantApplicationRequest
{
    @NotBlank(message = "Restaurant name is required.")
    private String restaurantName;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number.")
    private String phone;

    @NotBlank(message = "License document URL is required.")
    private String licenseDocumentUrl;

    @NotBlank(message = "Social media URL is required.")
    private String socialMediaUrl;

    private String socialMediaUrlOpt;

}
