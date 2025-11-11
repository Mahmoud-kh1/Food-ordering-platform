package com.foodapp.foodhub.service;

import com.foodapp.foodhub.dto.*;
import com.foodapp.foodhub.entity.OtpToken;
import com.foodapp.foodhub.entity.Token;
import com.foodapp.foodhub.entity.User;
import com.foodapp.foodhub.enums.TokenType;
import com.foodapp.foodhub.repository.OtpTokenRepository;
import com.foodapp.foodhub.repository.TokenRepository;
import com.foodapp.foodhub.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final OtpTokenRepository otpTokenRepository;
    private final JwtAuthService jwtService;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    private final EmailService emailService;

    private  final PasswordEncoder passwordEncoder;


    private final int OTP_EXPIRY_MINUTES = 5;
    private final int OTP_WAIT_SECONDS = 60;
    private final RestClient.Builder builder;

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

            revokeAllUserTokens(user, TokenType.ACCESS);
            revokeAllUserTokens(user, TokenType.REFRESH);

            saveToken(token, TokenType.ACCESS, user);
            saveToken(refreshToken, TokenType.REFRESH, user);

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

    public PasswordResponse sendOtp(ForgetPasswordRequest request) throws MessagingException {
        User user = userRepository.findByEmail(request.getEmail());
        if(user == null) {
            return PasswordResponse.builder()
                    .success(false)
                    .message("user not found")
                    .build();
        }
        OtpToken lastOtp = otpTokenRepository.findTopByUserOrderByCreatedAtDesc(user);
        if (lastOtp != null && lastOtp.getCreatedAt().plusSeconds(OTP_WAIT_SECONDS).isAfter(LocalDateTime.now())) {
            return PasswordResponse.builder().message( "Please wait 1 minute before requesting a new OTP")
                    .success(false)
                    .build();
        }
        String otp = String.format("%06d", new Random().nextInt(999999));
        OtpToken otpToken =
                OtpToken.builder()
                .user(user)
                .otp(otp)
                .createdAt(LocalDateTime.now())
                .expiryTime(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .build();
        otpTokenRepository.save(otpToken);
        emailService.sendOtpEmail(user.getEmail(), otp, OTP_EXPIRY_MINUTES);

        return PasswordResponse.builder().success(true)
                .message("OTP send Successfully to " + request.getEmail())
                .build();


    }


    public PasswordResponse resetPassword(ResetPasswordRequest request)  {
        User user = userRepository.findByEmail(request.getEmail());
        if(user == null) {
            return PasswordResponse.builder()
                    .message("user not found")
                    .success(false)
                    .build();
        }
     OtpToken otpOpt = otpTokenRepository.findByUserAndOtpAndExpiryTimeAfter(user, request.getOtp(), LocalDateTime.now());
        if (otpOpt == null){
            return PasswordResponse.builder()
                    .message("invalid or expired OTP")
                    .success(false)
                    .build();
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        otpTokenRepository.delete(otpOpt);
        userRepository.save(user);

        return PasswordResponse.builder()
                .message("Password reset successfully")
                .success(true)
                .build();

    }






    void saveToken( String token, TokenType tokenType, User user) {
        var savedToken = Token.builder()
                .user(user)
                .token(token)
                .tokenType(tokenType)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(savedToken);

    }

    void revokeAllUserTokens(User user, TokenType tokenType) {
        tokenRepository.revokeAllByType(user.getId(), tokenType);
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

        if (!jwtService.isTokenValid(refreshToken, user, TokenType.REFRESH)) {
            return buildFailedResponse("Invalid or expired refresh token", refreshToken);
        }

        var accessToken = jwtService.generateToken(user);

        revokeAllUserTokens(user, TokenType.ACCESS);
        saveToken(accessToken, TokenType.ACCESS, user);

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

    public PasswordResponse changePassword(ChangePasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            return PasswordResponse.builder()
                    .message("user not found")
                    .success(false)
                    .build();

        }
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return PasswordResponse.builder()
                    .message("old password is incorrect")
                    .success(false)
                    .build();
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return PasswordResponse.builder()
                .message("password changed successfully")
                .success(true)
                .build();

    }
}