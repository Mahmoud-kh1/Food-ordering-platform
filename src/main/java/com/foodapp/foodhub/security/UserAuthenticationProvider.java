package com.foodapp.foodhub.security;


import com.foodapp.foodhub.entity.User;
import com.foodapp.foodhub.enums.Role;
import com.foodapp.foodhub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserAuthenticationProvider implements AuthenticationProvider {


    private  final PasswordEncoder passwordEncoder;

    private  final UserService userService;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
          String username = authentication.getName();
          String entredPassword =  authentication.getCredentials().toString().trim();
          User user = userService.findByUsername(username);

          if((null != user) && passwordEncoder.matches(entredPassword, user.getPassword()) ) {
             return new UsernamePasswordAuthenticationToken(username, null, getGrantedAuthorities(user.getRole()));
          }
           else{
            throw new BadCredentialsException("Invalid credentials!");
         }
     }

    private List<GrantedAuthority> getGrantedAuthorities(Role roles) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_"+roles.toString()));
        return grantedAuthorities;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
