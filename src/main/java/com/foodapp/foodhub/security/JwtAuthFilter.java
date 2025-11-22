package com.foodapp.foodhub.security;
import com.foodapp.foodhub.entity.Token;
import com.foodapp.foodhub.entity.User;
import com.foodapp.foodhub.enums.Role;
import com.foodapp.foodhub.enums.TokenType;
import com.foodapp.foodhub.repository.TokenRepository;
import com.foodapp.foodhub.service.JwtAuthService;
import com.foodapp.foodhub.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtAuthService jwtAuthService;

    private final UserService userService;
    private final TokenRepository tokenRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        if (request.getServletPath().contains("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);
        username =  jwtAuthService.extractUsername(jwt);
        Token token = tokenRepository.findByToken(jwt);
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = userService.findByUsername(username);
            if(jwtAuthService.isTokenValid(jwt, user, TokenType.ACCESS) && token != null && !token.isExpired() && !token.isRevoked()) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        getGrantedAuthorities(user.getRole())
                );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);

    }

    private List<GrantedAuthority> getGrantedAuthorities(Role roles) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_"+roles.toString()));
        return grantedAuthorities;
    }
}
