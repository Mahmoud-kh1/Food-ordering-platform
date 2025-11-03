package com.foodapp.foodhub.controller;

import com.foodapp.foodhub.dto.AuthenticationRequest;
import com.foodapp.foodhub.dto.AuthenticationResponse;
import com.foodapp.foodhub.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;


    @GetMapping("/public")
    public String publicPage(){
        return "publicPage";
    }

    @GetMapping("/private")
    public String privatePage(){
        return "privatePage";
    }
//eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIwMTAyNzgxOTUwNSIsImlhdCI6MTc2MjE4MDI0OCwiZXhwIjoxNzYyNzg1MDQ4fQ.2s-QWjI5z-HZihh7KBdU4QpXF_k_7N1HlCvkbQswFL4
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
