package com.foodapp.foodhub.controller;

import com.foodapp.foodhub.dto.*;
import com.foodapp.foodhub.enums.TokenType;
import com.foodapp.foodhub.repository.TokenRepository;
import com.foodapp.foodhub.repository.UserRepository;
import com.foodapp.foodhub.service.AuthenticationService;
import com.foodapp.foodhub.service.JwtAuthService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private JavaMailSender mailSender;

    private final AuthenticationService authenticationService;
    private final JwtAuthService jwtAuthService;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;


    @PostMapping("/private")
    public String page(){
        return "hello , VIP";
    }

    @PostMapping("/mail")
    public void  mail(){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("yyynny4@gmail.com");
        message.setText("is there news about Freida?");
        message.setTo("abdomostfa2004@gmail.com");
        mailSender.send(message);
    }


    @PostMapping("/forget-password")
    public ResponseEntity<PasswordResponse> forgetPassword(@RequestBody ForgetPasswordRequest request) throws MessagingException {
      return  ResponseEntity.ok(authenticationService.sendOtp(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<PasswordResponse> resetPassword(@RequestBody ResetPasswordRequest request) throws MessagingException {
        return  ResponseEntity.ok(authenticationService.resetPassword(request));
    }

    @PostMapping("/change-password")
    public ResponseEntity<PasswordResponse> changePassword(@RequestBody ChangePasswordRequest request) throws MessagingException {
        return  ResponseEntity.ok(authenticationService.changePassword(request));
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No token found");
        }

        String accessToken = authHeader.substring(7);
        String phone = jwtAuthService.extractUsername(accessToken);
        var user = userRepository.findByUsername(phone);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        tokenRepository.revokeAllByType(user.getId(), TokenType.ACCESS);
        tokenRepository.revokeAllByType(user.getId(), TokenType.REFRESH);

        return ResponseEntity.ok("Logged out");
    }


    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> auth(@RequestBody AuthenticationRequest request){
        AuthenticationResponse authenticationResponse = authenticationService.authenticate(request);
        if(authenticationResponse.getStatus().equals("Failed")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authenticationResponse);
        }
        return ResponseEntity.ok(authenticationResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            AuthenticationResponse authResponse = authenticationService.refreshToken(request, response);

            if ("Failed".equals(authResponse.getStatus())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authResponse);
            }

            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            AuthenticationResponse errorResponse = AuthenticationResponse.builder()
                    .status("Failed")
                    .message("Error during token refresh: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }



}
