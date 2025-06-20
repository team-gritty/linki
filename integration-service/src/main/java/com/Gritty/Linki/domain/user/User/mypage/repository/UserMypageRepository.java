package com.Gritty.Linki.domain.user.User.mypage.repository;

import com.Gritty.Linki.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserMypageRepository extends JpaRepository<User, String> {
    Optional<User> findByUserId(String userId);
} 