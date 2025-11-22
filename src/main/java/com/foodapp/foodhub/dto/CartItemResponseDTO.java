package com.foodapp.foodhub.dto;

import lombok.*;

import java.math.BigDecimal;
@Getter
@Setter
public class CartItemResponseDTO {
    private String mealName;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal subTotal;
}