package com.linki.admin_integration_service.domain.operations.service;

import com.linki.admin_integration_service.domain.operations.dto.InfluencerReviewDTO;
import com.linki.admin_integration_service.domain.operations.repository.InfluencerReviewsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InfluencerReviewsServiceImpl implements InfluencerReviewsService {

    private final InfluencerReviewsMapper influencerReviewsMapper;


    @Override
    /**
     * 인플루언서 리뷰 목록을 조회합니다.
     * <p>
     * 뷰 테이블(influencer_reviews_view)을 조회하여
     * 인플루언서와 광고주의 정보를 포함한 리뷰 데이터를 반환합니다.
     *
     * @return 인플루언서 리뷰 DTO 리스트
     */
    public List<InfluencerReviewDTO> getAllInfluencerReviews() {
        List<InfluencerReviewDTO> influencerReviewDTOs = influencerReviewsMapper.getAllInfluencerReviews();

        if(influencerReviewDTOs.isEmpty()){
            return Collections.emptyList();
        }

        return influencerReviewDTOs;
    }
}
