package com.ssg.subscribeservice.repository;

import com.ssg.subscribeservice.entity.SubscribeEntity;
import com.ssg.subscribeservice.subsenum.SubscribeCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscribeRepository extends JpaRepository<SubscribeEntity, String> {
    // SubscribeCode 기준으로 값 조회
    Optional<SubscribeEntity> findBySubscribeCode(SubscribeCode subscribeCode);
}
