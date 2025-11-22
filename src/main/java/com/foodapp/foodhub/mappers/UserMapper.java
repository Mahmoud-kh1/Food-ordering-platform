package com.foodapp.foodhub.mappers;

import com.foodapp.foodhub.dto.RestaurantDto;
import com.foodapp.foodhub.dto.UserDto;
import com.foodapp.foodhub.entity.User;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper
{
    public UserDto mapToDto(User user)
    {
        UserDto dto = new UserDto();
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        if (user.getRestaurants() != null)
        {
            Set<RestaurantDto> restaurantDtos = user.getRestaurants().stream()
                    .map(r ->
                    {
                        RestaurantDto rd = new RestaurantDto();
                        rd.setId(r.getId());
                        rd.setName(r.getName());
                        rd.setPhone(r.getPhone());
                        rd.setStatus(r.getStatus());
                        return rd;
                    })
                    .collect(Collectors.toSet());
            dto.setRestaurants(restaurantDtos);
        }
        return dto;
    }
}
