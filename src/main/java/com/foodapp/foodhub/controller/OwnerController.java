package com.foodapp.foodhub.controller;
import com.foodapp.foodhub.dto.CompleteRestaurantDataRequest;
import com.foodapp.foodhub.dto.RestaurantDto;
import com.foodapp.foodhub.entity.User;
import com.foodapp.foodhub.service.RestaurantApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/owner")
@RequiredArgsConstructor
public class OwnerController
{
    private final RestaurantApplicationService restaurantService;

    @PutMapping("/completeRestaurantData")
    public ResponseEntity<RestaurantDto> completeRestaurantData(@AuthenticationPrincipal User user,
            @Valid @RequestBody CompleteRestaurantDataRequest request)
    {

        RestaurantDto updated = restaurantService.completeRestaurantData(user, request);
        return ResponseEntity.ok(updated);
    }
}
