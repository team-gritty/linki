package com.Gritty.Linki.domain.user.influencer.pay.repository;

import com.Gritty.Linki.domain.user.influencer.dto.PayUserDto;
import com.Gritty.Linki.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InfluencerPayRepository extends JpaRepository<User, String> {
    //유저 디티오 가져옴
    public Optional<User> findByUserId(String userId);
}
