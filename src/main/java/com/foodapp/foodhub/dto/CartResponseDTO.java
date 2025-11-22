package com.foodapp.foodhub.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CartResponseDTO {
    private Long cartId;
    private BigDecimal totalPrice;
    private List<CartItemResponseDTO> items;
}