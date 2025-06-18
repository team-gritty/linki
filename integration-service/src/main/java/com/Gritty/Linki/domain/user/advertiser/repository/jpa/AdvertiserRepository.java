package com.Gritty.Linki.domain.user.advertiser.repository.jpa;

import com.Gritty.Linki.entity.Advertiser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 광고주 JPA 레포지토리
 */
@Repository
public interface AdvertiserRepository extends JpaRepository<Advertiser, String> {

    /**
     * 사용자 ID로 광고주 조회
     * 
     * @param userId 사용자 ID
     * @return 광고주 Optional
     */
    @Query("SELECT a FROM Advertiser a WHERE a.user.userId = :userId")
    Optional<Advertiser> findByUser_UserId(@Param("userId") String userId);

}