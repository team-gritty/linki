package com.Gritty.Linki.domain.user.common.home.repository;

import com.Gritty.Linki.entity.LinkiScore;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LinkiScoreRepository extends JpaRepository<LinkiScore, String> {

    /**
     * 가중치 적용한 총 점수를 기준으로 상위 인플루언서 조회
     * 
     * @param pageable 페이징 정보 (limit 역할)
     * @return 추천 인플루언서 목록 (점수 높은 순)
     */
    @Query("""
            SELECT ls, i, u, c
            FROM LinkiScore ls
            INNER JOIN Influencer i ON ls.influencerId = i.influencerId
            INNER JOIN User u ON i.userId = u.userId
            LEFT JOIN Channel c ON c.influencer.influencerId = i.influencerId
            WHERE ls.costPerClick IS NOT NULL
            AND ls.dailyTraffic IS NOT NULL
            AND ls.averageReviewScore IS NOT NULL
            AND ls.contractCount IS NOT NULL
            ORDER BY (
                (COALESCE(ls.costPerClick, 0) * 0.3) +
                (COALESCE(ls.dailyTraffic, 0) * 0.25) +
                (COALESCE(ls.averageReviewScore, 0) * 0.25) +
                (COALESCE(ls.contractCount, 0) * 0.2)
            ) DESC
            """)
    List<Object[]> findTopInfluencersByScore(Pageable pageable);

    /**
     * 점수가 있는 인플루언서 수 조회 (테스트용)
     */
    @Query("SELECT COUNT(ls) FROM LinkiScore ls WHERE ls.costPerClick IS NOT NULL")
    long countValidScores();
}