package com.foodapp.foodhub.mappers;


import com.foodapp.foodhub.dto.CartItemResponseDTO;
import com.foodapp.foodhub.dto.CartResponseDTO;
import com.foodapp.foodhub.entity.Cart;
import com.foodapp.foodhub.entity.CartItem;

import java.math.BigDecimal;

public class CartMapper {
    public static CartResponseDTO toCartResponseDTO(Cart cart) {
        CartResponseDTO dto = new CartResponseDTO();
        dto.setCartId(cart.getId());

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : cart.getItems()) {
            CartItemResponseDTO itemDTO = new CartItemResponseDTO();

            itemDTO.setMealName(item.getMealName());
            itemDTO.setUnitPrice(item.getUnitPrice());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setSubTotal(item.getSubTotal());

            dto.getItems().add(itemDTO);

            if (item.getSubTotal() != null) {
                total = total.add(item.getSubTotal());
            }
        }

        dto.setTotalPrice(total);

        return dto;
    }
}