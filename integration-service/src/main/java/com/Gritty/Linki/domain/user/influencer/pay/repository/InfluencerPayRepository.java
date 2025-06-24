package com.Gritty.Linki.domain.user.influencer.pay.repository;

import com.Gritty.Linki.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InfluencerPayRepository extends JpaRepository<User, String> {
    //유저 엔티티
    public Optional<User> findByUserId(String userId);
}
