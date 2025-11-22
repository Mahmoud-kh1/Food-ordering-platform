package com.foodapp.foodhub.repository;

import com.foodapp.foodhub.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository <CartItem, Long > {
}