package com.linki.admin_integration_service.domain.operations.service;

import com.linki.admin_integration_service.domain.operations.dto.InfluencerReviewDTO;
import com.linki.admin_integration_service.domain.operations.dto.InfluencerReviewSearchRequestDTO;
import com.linki.admin_integration_service.domain.operations.dto.InfluencerReviewVisibilityRequestDTO;
import com.linki.admin_integration_service.domain.operations.repository.myBatis.InfluencerReviewsMapper;
import com.linki.admin_integration_service.entity.InfluencerReview;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class InfluencerReviewsServiceImpl implements InfluencerReviewsService {

    private final InfluencerReviewsMapper influencerReviewsMapper;

    @PersistenceContext
    private EntityManager em;


    /**
     * 인플루언서 리뷰 전체 목록을 조회합니다.
     * <p>
     * 뷰 테이블(influencer_reviews_view)을 조회하여
     * 인플루언서, 광고주, 리뷰 정보가 포함된 데이터를 반환합니다.
     *
     * @return 전체 인플루언서 리뷰 DTO 리스트 (없을 경우 빈 리스트 반환)
     */
    @Override
    public List<InfluencerReviewDTO> getAllInfluencerReviews() {
        List<InfluencerReviewDTO> influencerReviewDTOs = influencerReviewsMapper.getAllInfluencerReviews();

        if(influencerReviewDTOs.isEmpty()){
            return Collections.emptyList();
        }

        return influencerReviewDTOs;
    }

    /**
     * 특정 인플루언서 리뷰의 공개 여부를 수정합니다.
     * <p>
     * 리뷰 ID로 해당 리뷰를 조회하여 visibility 필드를 갱신한 후 저장합니다.
     *
     * @param influencerReviewVisibilityRequestDTO 리뷰 ID와 수정할 공개 여부
     * @return 수정 성공 여부 (true: 성공, false: 실패)
     */
    @Override
    @Transactional
    public Boolean updateInfluencerReviewVisibility(InfluencerReviewVisibilityRequestDTO influencerReviewVisibilityRequestDTO) {

        InfluencerReview influencerReview = em.find(InfluencerReview.class, influencerReviewVisibilityRequestDTO.getId());

        if(influencerReview == null){
            return false;
        }

        influencerReview.setVisibility(influencerReviewVisibilityRequestDTO.getVisibility());
        em.merge(influencerReview);

        return true;
    }

    /**
     * 검색 조건에 따라 인플루언서 리뷰 목록을 조회합니다.
     * <p>
     * 검색어(keyword)가 비어 있거나 null인 경우 전체 리뷰를 조회하고,
     * 그렇지 않은 경우 주어진 searchType에 따라 조건 검색을 수행합니다.
     * <p>
     * searchType은 프론트에서 드롭다운으로 전달되며, 정규화(lowercase + trim) 처리 후 쿼리에 사용됩니다.
     *
     * @param influencerReviewSearchRequestDTO 검색 조건 (searchType, keyword 포함)
     * @return 필터링된 인플루언서 리뷰 DTO 리스트
     */
    @Override
    public List<InfluencerReviewDTO> searchAllInfluencerReviews(InfluencerReviewSearchRequestDTO influencerReviewSearchRequestDTO) {
        String keyword = influencerReviewSearchRequestDTO.getKeyword();
        String searchType = influencerReviewSearchRequestDTO.getSearchType();

        // 1. 둘 다 비었을 경우 → 전체 조회
        if ((keyword == null || keyword.isBlank()) && (searchType == null || searchType.isBlank())) {
            return influencerReviewsMapper.getAllInfluencerReviews();
        }

        // 2. keyword는 있는데 searchType이 없으면 → 의미 없으므로 빈 리스트
        if ((searchType == null || searchType.isBlank()) && keyword != null && !keyword.isBlank()) {
            return Collections.emptyList();
        }

        // 3. keyword 없는데 searchType만 있으면 → 의미 없으므로 빈 리스트
        if ((keyword == null || keyword.isBlank())) {
            return Collections.emptyList();
        }

        // 4. 정상 검색
        influencerReviewSearchRequestDTO.setSearchType(searchType.trim().toLowerCase(Locale.ROOT));
        List<InfluencerReviewDTO> result = influencerReviewsMapper.searchInfluencerReviews(influencerReviewSearchRequestDTO);
        return result.isEmpty() ? Collections.emptyList() : result;
    }


}
