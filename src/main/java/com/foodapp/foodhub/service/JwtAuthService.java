package com.foodapp.foodhub.service;

import com.foodapp.foodhub.entity.Token;
import com.foodapp.foodhub.entity.User;
import com.foodapp.foodhub.enums.TokenType;
import com.foodapp.foodhub.repository.TokenRepository;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
@AllArgsConstructor

@Setter
@Getter
public class JwtAuthService {

    @Value("${jwt.algorithm.key}")
    public   String secretKey;

    @Value("${jwt.access.expiration}")
    private long accsesExpiration;

    @Value("${jwt.refresh.expiration}")
    private long refreshExpiration;
    private TokenRepository tokenRepository;

    @Autowired
    public JwtAuthService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(User user) {
        return generateToken(new HashMap<>(), user);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            User user
    ) {
        return buildToken(extraClaims, user, accsesExpiration);
    }

    public String generateRefreshToken(
            User user
    ) {

        return buildToken(new HashMap<>(), user, refreshExpiration);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            User user,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, User user, TokenType type) {
        final String username = extractUsername(token);
        Token optionalToken = tokenRepository.findByToken(token);
        if(optionalToken == null || optionalToken.getTokenType() != type) {
            return false;
        }
        return (username.equals(user.getUsername())) && !isTokenExpired(token) && !optionalToken.isExpired() && !optionalToken.isRevoked();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
