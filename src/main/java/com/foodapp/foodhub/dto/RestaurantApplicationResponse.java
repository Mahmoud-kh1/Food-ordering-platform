package com.foodapp.foodhub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RestaurantApplicationResponse
{
    private Long id;
    private Long ownerId;
    private String restaurantName;
    private String phone;
    private String city;
    private String licenseDocumentUrl;
    private String socialMediaUrl;
    private String socialMediaUrlOpt;
    private String status;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private String reviewedBy;
}
