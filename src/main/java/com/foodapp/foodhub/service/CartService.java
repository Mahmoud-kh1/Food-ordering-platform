package com.foodapp.foodhub.service;

import com.foodapp.foodhub.entity.*;
import com.foodapp.foodhub.repository.CartItemRepository;
import com.foodapp.foodhub.repository.CartRepository;
import com.foodapp.foodhub.repository.MealRepository;
import com.foodapp.foodhub.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class CartService {


        private final CartRepository cartRepository;
        private final CartItemRepository cartItemRepository;
        private final UserRepository userRepository;
        private final MealRepository mealRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, UserRepository userRepository, MealRepository mealRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.mealRepository = mealRepository;
    }

    public Cart addItemToCart(Long userId, Long mealId, int quantity) {

            // Find or create cart
            Cart cart = cartRepository.findByUserId(userId)
                    .orElseGet(() -> {
                        Cart newCart = new Cart();
                        newCart.setUser(userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found")));
                        return cartRepository.save(newCart);
                    });

            Meal meal = mealRepository.findById(mealId)
                    .orElseThrow(() -> new RuntimeException("Meal not found"));

             Restaurant mealRestaurant = meal.getRestaurant();

            if (cart.getRestaurant() == null) {
                cart.setRestaurant(mealRestaurant);
            }

            // 2️⃣ If restaurant does not match → user is mixing restaurants
            if (!cart.getRestaurant().getId().equals(mealRestaurant.getId())) {
                throw new RuntimeException("You cannot add meals from different restaurants in one cart.");
            }
            // 3️⃣ Check if item already exists in cart
            CartItem existingItem = cart.getItems().stream()
                    .filter(item -> item.getMeal().getId().equals(mealId))
                    .findFirst()
                    .orElse(null);

            if (existingItem != null) {
                // 4️⃣ If exists, update quantity
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
            } else {
                // 5️⃣ Else create new item
                CartItem newItem = new CartItem();
                newItem.setMeal(meal);
                newItem.setMealName(meal.getName());
                newItem.setUnitPrice(meal.getPrice());
                newItem.setQuantity(quantity);
                newItem.setCart(cart);

                cart.getItems().add(newItem);
            }

            // 6️⃣ Save and return updated cart
            return cartRepository.save(cart);
        }

    public String removeItemFromCart(Long userId, Long mealId) {

        // 1️⃣ Find the user's cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        // 2️⃣ Find the item to remove
        CartItem itemToRemove = cart.getItems().stream()
                .filter(item -> item.getMeal().getId().equals(mealId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        // 3️⃣ Remove from cart set
        cart.getItems().remove(itemToRemove);

        // 4️⃣ Optional: delete from repository if needed
        cartItemRepository.delete(itemToRemove);

        // 5️⃣ Save cart
       cartRepository.save(cart);
        return "Item removed successfully ";
    }


    public Cart getCart( Long userId){
        return cartRepository.findByUserId(userId)
                .orElseThrow(()-> new RuntimeException("Cart not found"));

    }
}

