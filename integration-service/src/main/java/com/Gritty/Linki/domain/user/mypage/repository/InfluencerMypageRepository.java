package com.Gritty.Linki.domain.user.mypage.repository;

import com.Gritty.Linki.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InfluencerMypageRepository extends JpaRepository<User, String> {

    @Query("SELECT u FROM User u WHERE u.userId = :userId AND u.userRole = 'ROLE_INFLUENCER'")
    Optional<User> findByUserId(@Param("userId") String userId);
} 