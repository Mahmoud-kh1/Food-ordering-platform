package com.foodapp.foodhub.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PasswordResponse {
    boolean success;
    String message;
}
