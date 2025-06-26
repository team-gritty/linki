package com.Gritty.Linki.domain.user.common.home.service;

import com.Gritty.Linki.domain.user.common.home.dto.RecommendedInfluencerDTO;

import java.util.List;

public interface HomeRecommendationService {

    /**
     * 이번 달 추천 인플루언서 목록 조회
     * linki score를 기반으로 가중치를 적용하여 상위 인플루언서를 추천
     * 
     * @param limit 조회할 인플루언서 수 (기본값: 10)
     * @return 추천 인플루언서 목록
     */
    List<RecommendedInfluencerDTO> getRecommendedInfluencers(int limit);
}