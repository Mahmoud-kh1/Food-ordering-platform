package com.foodapp.foodhub.config;

import com.foodapp.foodhub.security.JwtAuthFilter;
import com.foodapp.foodhub.security.UserAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserAuthenticationProvider userAuthenticationProvider;
    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       http.csrf(csrf -> csrf.disable());
        http.authorizeHttpRequests(
                        authorizeConfig ->{
                            authorizeConfig.requestMatchers("/api/auth/login").permitAll();
                            authorizeConfig.requestMatchers("/api/auth/refresh").permitAll();
                            authorizeConfig.requestMatchers("/api/auth/logout").permitAll();
                            authorizeConfig.requestMatchers("/api/auth/forget-password").permitAll();
                            authorizeConfig.requestMatchers("/api/auth/reset-password").permitAll();

                            authorizeConfig.anyRequest().authenticated();
                          }
                        )
                .authenticationProvider(userAuthenticationProvider)
                        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
           http.httpBasic(customizer ->{});

        return http.build();
    }




}
