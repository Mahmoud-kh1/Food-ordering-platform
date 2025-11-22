package com.foodapp.foodhub.mappers;

import com.foodapp.foodhub.dto.RestaurantApplicationDto;
import com.foodapp.foodhub.dto.RestaurantApplicationRequest;
import com.foodapp.foodhub.dto.RestaurantApplicationResponse;
import com.foodapp.foodhub.dto.RestaurantDto;
import com.foodapp.foodhub.entity.Restaurant;
import com.foodapp.foodhub.entity.RestaurantApplication;
import com.foodapp.foodhub.entity.User;
import com.foodapp.foodhub.enums.RestaurantApplicationStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RestaurantApplicationMapper
{
    public RestaurantApplication toEntity(RestaurantApplicationRequest request, User user)
    {
        return RestaurantApplication.builder()
                .owner(user)
                .restaurantName(request.getRestaurantName())
                .phone(request.getPhone())
                .licenseDocumentUrl(request.getLicenseDocumentUrl())
                .socialMediaUrl(request.getSocialMediaUrl())
                .socialMediaUrlOpt(request.getSocialMediaUrlOpt())
                .status(RestaurantApplicationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }
    public RestaurantApplicationResponse toResponse(RestaurantApplication app) {
        return RestaurantApplicationResponse.builder()
                .id(app.getId())
                .ownerId(app.getOwner().getId())
                .restaurantName(app.getRestaurantName())
                .phone(app.getPhone())
                .licenseDocumentUrl(app.getLicenseDocumentUrl())
                .socialMediaUrl(app.getSocialMediaUrl())
                .socialMediaUrlOpt(app.getSocialMediaUrlOpt())
                .status(app.getStatus().name())
                .rejectionReason(app.getRejectionReason()!= null ? app.getRejectionReason() : null)
                .createdAt(app.getCreatedAt())
                .reviewedBy(app.getReviewedBy() != null ? app.getReviewedBy().getUsername() : null)
                .build();
    }
    public RestaurantApplicationDto appToDto(RestaurantApplication app)
    {
        return RestaurantApplicationDto.builder()
                .id(app.getId())
                .restaurantName(app.getRestaurantName())
                .status(app.getStatus().name())
                .rejectionReason(app.getRejectionReason() != null ? app.getRejectionReason() : null)
                .reviewedBy(app.getReviewedBy() != null ? app.getReviewedBy().getUsername() : null)
                .build();
    }
    public RestaurantDto restaurantToDto(Restaurant restaurant) {
        return RestaurantDto.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .phone(restaurant.getPhone())
                .imageUrl(restaurant.getImageUrl())
                .address(restaurant.getAddress())
                .rating(restaurant.getRating())
                .status(restaurant.getStatus())
                .subcategoryId(restaurant.getSubcategory() != null ? restaurant.getSubcategory().getId() : null)
                .subcategoryName(restaurant.getSubcategory() != null ? restaurant.getSubcategory().getName() : null)
                .categoryId(restaurant.getSubcategory() != null ? restaurant.getSubcategory().getCategory().getId() : null)
                .categoryName(restaurant.getSubcategory() != null ? restaurant.getSubcategory().getCategory().getName() : null)
                .build();
    }
}
