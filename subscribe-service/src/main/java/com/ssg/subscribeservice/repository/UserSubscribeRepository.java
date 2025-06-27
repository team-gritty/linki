package com.ssg.subscribeservice.repository;


import com.ssg.subscribeservice.entity.UserSubscribeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSubscribeRepository extends JpaRepository<UserSubscribeEntity, String> {
    Optional<UserSubscribeEntity> findByUserId(String userId);
}
