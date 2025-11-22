package com.foodapp.foodhub.service;
import com.foodapp.foodhub.dto.RestaurantApplicationDto;
import com.foodapp.foodhub.dto.UserDto;
import com.foodapp.foodhub.entity.RestaurantApplication;
import com.foodapp.foodhub.entity.User;
import com.foodapp.foodhub.enums.UserStatus;
import com.foodapp.foodhub.mappers.RestaurantApplicationMapper;
import com.foodapp.foodhub.mappers.UserMapper;
import com.foodapp.foodhub.repository.RestaurantApplicationRepository;
import com.foodapp.foodhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
@Service
@RequiredArgsConstructor
public class UserService
{
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final RestaurantApplicationMapper restaurantApplicationMapper;
  private final RestaurantApplicationRepository restaurantApplicationRepository;

   public User findByUsername(String username) {
       return userRepository.findByUsername(username);
   }

   public List<UserDto> getAllUsers()
   {
       return userRepository.findAll().stream().map(userMapper::mapToDto).toList();
   }
    public RestaurantApplicationDto getMyApplication(User user)
    {
        RestaurantApplication app = restaurantApplicationRepository.findByOwner(user);
        return restaurantApplicationMapper.appToDto(app);
    }
    @Transactional
    public void deleteUserById(Long userId)
    {
        userRepository.deleteUserById(userId);
    }
    public void disabledUserById(@PathVariable Long userId)
    {
        User user = userRepository.findById(userId).get();
        user.setStatus(UserStatus.DISABLED);
    }

}
