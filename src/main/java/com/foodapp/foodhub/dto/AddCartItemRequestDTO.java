package com.foodapp.foodhub.dto;

import lombok.*;
@Setter
@Getter
public class AddCartItemRequestDTO {
    private Long userId;
    private Long mealId;
    private int quantity;
}