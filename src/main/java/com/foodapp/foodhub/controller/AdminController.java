package com.foodapp.foodhub.controller;

import com.foodapp.foodhub.dto.ApplicationReviewDTO;
import com.foodapp.foodhub.dto.RestaurantApplicationResponse;
import com.foodapp.foodhub.dto.UserDto;
import com.foodapp.foodhub.entity.User;
import com.foodapp.foodhub.mappers.RestaurantApplicationMapper;
import com.foodapp.foodhub.repository.RestaurantApplicationRepository;
import com.foodapp.foodhub.service.RestaurantApplicationService;
import com.foodapp.foodhub.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@RestController @RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController
{
    private final RestaurantApplicationService restaurantApplicationService;
    private final RestaurantApplicationRepository restaurantApplicationRepository;
    private final RestaurantApplicationMapper restaurantApplicationMapper;
    private final UserService userService;
    @PutMapping("/{appId}/review")
    public ResponseEntity<RestaurantApplicationResponse> reviewApplication(@AuthenticationPrincipal User admin, @PathVariable Long appId,
                                                                           @RequestBody @Valid ApplicationReviewDTO reviewDTO)
           // send notification here
    {
        RestaurantApplicationResponse response =
                restaurantApplicationService.reviewApplication(appId, admin, reviewDTO);
        response.setRejectionReason(reviewDTO.getRejectionReason());
        //Logic to send notification
        return ResponseEntity.ok(response);
    }

    @GetMapping("/applications")
    public List<RestaurantApplicationResponse> getAllApplications()
    {
        return restaurantApplicationRepository.findAll()
                .stream()
                .map(restaurantApplicationMapper::toResponse)
                .toList();
    }

    @GetMapping("/applications/{appId}")
    public Optional<RestaurantApplicationResponse> getApplicationByID(@PathVariable Long appId)
    {
        return restaurantApplicationRepository.findById(appId).map(restaurantApplicationMapper::toResponse);
    }

    @GetMapping("/users")
    public List<UserDto> getAllUsers()
    {
        return userService.getAllUsers();
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long userId)
    {
        userService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/users/{userId}")
    public ResponseEntity<String>disabledUserById(@PathVariable Long userId)
    {
        userService.disabledUserById(userId);
        return ResponseEntity.ok().build();
    }
}
