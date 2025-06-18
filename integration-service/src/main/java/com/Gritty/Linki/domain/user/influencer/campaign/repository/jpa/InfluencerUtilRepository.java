package com.Gritty.Linki.domain.user.influencer.campaign.repository.jpa;

import com.Gritty.Linki.entity.Influencer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * 인플루언서 JPA 레포지토리
 */
public interface InfluencerUtilRepository extends JpaRepository<Influencer, String> {
    /**
     * User 엔티티의 userId 로 인플루언서 조회
     *
     * @param userId 일반 회원 테이블의 PK
     * @return 인플루언서 Optional
     */
    @Query("SELECT i FROM Influencer i WHERE i.user.userId = :userId")
    Optional<Influencer> findByUser_UserId(@Param("userId") String userId);


}
