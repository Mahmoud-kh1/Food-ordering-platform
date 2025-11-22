package com.foodapp.foodhub.repository;

import com.foodapp.foodhub.entity.Token;
import com.foodapp.foodhub.entity.User;
import com.foodapp.foodhub.enums.TokenType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TokenRepository extends JpaRepository<Token, Long> {


    @Modifying
    @Transactional
    @Query("""
    UPDATE Token t
    SET t.expired = TRUE, t.revoked = TRUE
    WHERE t.user.id = :userId
    AND t.tokenType = :type
    AND (t.expired = FALSE OR t.revoked = FALSE)
""")
    void revokeAllByType(
            @Param("userId") Long userId,
            @Param("type") TokenType type
    );

    Token findByToken(String jwt);
}
