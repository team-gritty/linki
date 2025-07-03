package com.Gritty.Linki.domain.account.account.repository;

import com.Gritty.Linki.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    void deleteByRefreshToken(String refreshToken);
}
