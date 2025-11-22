package com.foodapp.foodhub.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.foodapp.foodhub.enums.TokenType;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    private boolean revoked;

    private boolean expired;

    @JsonProperty("expired")
    public boolean isExpired() {
        return expired;
    }
    @JsonProperty("expired")
    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    @JsonProperty("revoked")
    public boolean isRevoked() {
        return revoked;
    }
    @JsonProperty("revoked")
    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}