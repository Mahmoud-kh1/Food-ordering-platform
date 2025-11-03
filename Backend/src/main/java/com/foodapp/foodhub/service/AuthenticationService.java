package com.foodapp.foodhub.service;

import com.foodapp.foodhub.dto.AuthenticationRequest;
import com.foodapp.foodhub.dto.AuthenticationResponse;
import com.foodapp.foodhub.entity.Token;
import com.foodapp.foodhub.entity.User;
import com.foodapp.foodhub.enums.TokenType;
import com.foodapp.foodhub.repository.TokenRepository;
import com.foodapp.foodhub.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    private final JwtAuthService jwtService;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getUsername(),
                            authenticationRequest.getPassword()
                    )
            );
            User user = userService.findByUsername(authenticationRequest.getUsername());
            String token = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            revokeAllUserTokens(user);
            saveToken(token, user);
            return AuthenticationResponse.builder()
                    .accessToken(token)
                    .refreshToken(refreshToken)
                    .status("Success")
                    .message("you have been authenticated")
                    .build();
        }
        catch (AuthenticationException e) {
             return AuthenticationResponse.builder()
                     .status("Failed")
                     .message(e.getMessage())
                     .build();
        }
    }
    void saveToken(String token, User user) {
        var curToken = Token.builder()
                .user(user)
                .token(token)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(curToken);
    }

    void revokeAllUserTokens(User user) {
        tokenRepository.revokeAllUserTokens(user.getId());
    }

    public AuthenticationResponse refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return buildFailedResponse("No Refresh Token Found", null);
        }

        final String refreshToken = authHeader.substring(7);
        final String username = jwtService.extractUsername(refreshToken);

        if (username == null) {
            return buildFailedResponse("The token doesn't contain username", refreshToken);
        }

        var user = userRepository.findByUsername(username);
        if (user == null) {
            return buildFailedResponse("There is no such user", refreshToken);
        }

        if (!jwtService.isTokenValid(refreshToken, user)) {
            return buildFailedResponse("Invalid or expired refresh token", refreshToken);
        }

        var accessToken = jwtService.generateToken(user);

        revokeAllUserTokens(user);
        saveToken(accessToken, user);

        return AuthenticationResponse.builder()
                .status("success")
                .message("Refresh Token Success")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private AuthenticationResponse buildFailedResponse(String message, String refreshToken) {
        return AuthenticationResponse.builder()
                .status("Failed")
                .message(message)
                .refreshToken(refreshToken)
                .build();
    }

}