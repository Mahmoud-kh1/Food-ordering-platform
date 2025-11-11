package com.foodapp.foodhub.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Builder
@Data
@Setter
@Getter
public class ResetPasswordRequest {
    private String email;
    private String otp;
    private String newPassword;
}
