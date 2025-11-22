package com.foodapp.foodhub.service;
import com.foodapp.foodhub.dto.*;
import com.foodapp.foodhub.entity.*;
import com.foodapp.foodhub.enums.RestaurantApplicationStatus;
import com.foodapp.foodhub.enums.RestaurantStatus;
import com.foodapp.foodhub.enums.Role;
import com.foodapp.foodhub.enums.UserStatus;
import com.foodapp.foodhub.mappers.RestaurantApplicationMapper;
import com.foodapp.foodhub.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RestaurantApplicationService
{
    private final RestaurantApplicationRepository restaurantApplicationRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantApplicationMapper restaurantApplicationMapper;
    private final SubcategoryRepository subcategoryRepository;
    public RestaurantApplicationResponse createApplication (User user , RestaurantApplicationRequest request)
    {
        if (restaurantApplicationRepository.findByOwner(user)!=null) //already has an application
            throw new RuntimeException("You have already submitted an application. Wait for Admin Response");
        if (userRepository.findById(user.getId()).get().getStatus()== UserStatus.DISABLED)
            throw new RuntimeException("Your Account is disabled.");
        RestaurantApplication app = restaurantApplicationMapper.toEntity(request,user);
        RestaurantApplication saved = restaurantApplicationRepository.save(app);
        return restaurantApplicationMapper.toResponse(saved);
    }


    public RestaurantApplicationResponse reviewApplication(Long appId, User admin , ApplicationReviewDTO reviewDTO )
    {
        RestaurantApplication app = restaurantApplicationRepository.findById(appId)
                .orElseThrow(() -> new RuntimeException("Application not found."));
        if (restaurantApplicationRepository.findById(appId).get().getOwner().getStatus()==UserStatus.DISABLED)
            throw new RuntimeException("User's Account is disabled.");
        app.setStatus(reviewDTO.getStatus());
        app.setReviewedBy(admin);
        if (reviewDTO.getStatus() == RestaurantApplicationStatus.APPROVED)
        {
            Restaurant restaurant = Restaurant.builder()
                    .name(app.getRestaurantName())
                    .phone(app.getPhone())
                    .owner(app.getOwner())
                    .status(RestaurantStatus.ACTIVE)
                    .build();
            app.setRejectionReason(reviewDTO.getRejectionReason());
            app.getOwner().setRole(Role.RESTAURANT_OWNER);
            restaurantRepository.save(restaurant);
        }
        app.setRejectionReason(reviewDTO.getRejectionReason());
        restaurantApplicationRepository.save(app);
        return restaurantApplicationMapper.toResponse(restaurantApplicationRepository.save(app));
    }

    public RestaurantDto completeRestaurantData(User owner, CompleteRestaurantDataRequest request)
    {
        Restaurant restaurant = restaurantRepository.findRestaurantByOwner(owner);
        restaurant.setDescription(request.getDescription());
        restaurant.setAddress(request.getAddress());
        restaurant.setImageUrl(request.getImageUrl());
        if (request.getSubcategoryId() != null)
        {
            Subcategory subcategory = subcategoryRepository.findById(request.getSubcategoryId())
                    .orElseThrow(() -> new RuntimeException("Subcategory not found."));
            restaurant.setSubcategory(subcategory);
        }
//        if (request.getZoneIds() != null && !request.getZoneIds().isEmpty())
//        {
//            Set<Zone> zones = new HashSet<>(zoneRepository.findAllById(request.getZoneIds()));
//            restaurant.setZones(zones);
//        }
        restaurantRepository.save(restaurant);
        return restaurantApplicationMapper.restaurantToDto(restaurant);
    }
}
