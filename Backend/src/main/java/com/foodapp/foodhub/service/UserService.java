package com.foodapp.foodhub.service;


import com.foodapp.foodhub.entity.User;
import com.foodapp.foodhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
  @Autowired
  private final UserRepository userRepository;

   public User findByUsername(String username) {
       return userRepository.findByUsername(username);
   }

}
