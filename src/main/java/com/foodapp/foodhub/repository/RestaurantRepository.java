package com.foodapp.foodhub.repository;

import com.foodapp.foodhub.entity.Restaurant;
import com.foodapp.foodhub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Restaurant findRestaurantByOwner(User owner);

}