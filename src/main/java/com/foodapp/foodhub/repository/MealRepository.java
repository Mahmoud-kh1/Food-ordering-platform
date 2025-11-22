package com.foodapp.foodhub.repository;

import com.foodapp.foodhub.entity.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MealRepository extends JpaRepository<Meal,Long> {

    // Find all meals by restaurant for restaurant admin preview
    List<Meal> findByRestaurantId(Long restaurantId);

    // Find available meals for user preview
    List<Meal> findByRestaurantIdAndIsAvailableTrue(Long restaurantId);
}
