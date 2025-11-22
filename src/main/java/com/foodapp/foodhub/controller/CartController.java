package com.foodapp.foodhub.controller;


import com.foodapp.foodhub.dto.AddCartItemRequestDTO;
import com.foodapp.foodhub.dto.CartResponseDTO;
import com.foodapp.foodhub.entity.Cart;
import com.foodapp.foodhub.mappers.CartMapper;
import com.foodapp.foodhub.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/cart/add")
    public ResponseEntity<CartResponseDTO> addItem(
            @RequestBody AddCartItemRequestDTO request
    ) {
        Cart cart = cartService.addItemToCart(
                request.getUserId(),
                request.getMealId(),
                request.getQuantity()
        );
        CartResponseDTO response = CartMapper.toCartResponseDTO(cart);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/remove")
    public ResponseEntity<String> removeFromCart(@RequestParam Long userId , @RequestParam Long mealId ) {
        String removedStatus = cartService.removeItemFromCart(userId, mealId);
        return ResponseEntity.ok(removedStatus);
    }

    @GetMapping("/cart")
    public ResponseEntity<Cart> getUserCart ( @RequestParam Long userId){
        return ResponseEntity.ok(cartService.getCart(userId));
    }

}
