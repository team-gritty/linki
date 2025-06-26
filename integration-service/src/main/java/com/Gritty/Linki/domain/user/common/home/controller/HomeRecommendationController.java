package com.Gritty.Linki.domain.user.common.home.controller;

import com.Gritty.Linki.domain.user.common.home.dto.RecommendedInfluencerDTO;
import com.Gritty.Linki.domain.user.common.home.repository.LinkiScoreRepository;
import com.Gritty.Linki.domain.user.common.home.response.RecommendedInfluencerResponse;
import com.Gritty.Linki.domain.user.common.home.service.HomeRecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/v1/api/home")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class HomeRecommendationController {

    private final HomeRecommendationService homeRecommendationService;

    /**
     * 이번 달 추천 인플루언서 목록 조회
     * 
     * @param limit 조회할 인플루언서 수 (기본값: 10, 최대: 50)
     * @return 추천 인플루언서 목록
     */
    @GetMapping("/recommended-influencers")
    public ResponseEntity<RecommendedInfluencerResponse> getRecommendedInfluencers(
            @RequestParam(value = "limit", defaultValue = "10") int limit) {

        log.info("이번 달 추천 인플루언서 조회 요청 - limit: {}", limit);

        // limit 범위 검증
        if (limit < 1)
            limit = 10;
        if (limit > 50)
            limit = 50;

        try {
            List<RecommendedInfluencerDTO> recommendedInfluencers = homeRecommendationService
                    .getRecommendedInfluencers(limit);

            RecommendedInfluencerResponse response = RecommendedInfluencerResponse.of(recommendedInfluencers);

            log.info("이번 달 추천 인플루언서 조회 성공 - 조회된 수: {}", recommendedInfluencers.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("이번 달 추천 인플루언서 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

}