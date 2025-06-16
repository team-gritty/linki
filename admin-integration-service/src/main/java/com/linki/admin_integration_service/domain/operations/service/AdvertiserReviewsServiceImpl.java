package com.linki.admin_integration_service.domain.operations.service;

import com.linki.admin_integration_service.domain.operations.dto.AdvertiserReviewDTO;
import com.linki.admin_integration_service.domain.operations.dto.AdvertiserReviewSearchRequestDTO;
import com.linki.admin_integration_service.domain.operations.dto.AdvertiserReviewVisibilityRequestDTO;
import com.linki.admin_integration_service.domain.operations.dto.InfluencerReviewDTO;
import com.linki.admin_integration_service.domain.operations.repository.myBatis.AdvertiserReviewsMapper;
import com.linki.admin_integration_service.entity.AdvertiserReview;
import com.linki.admin_integration_service.entity.InfluencerReview;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Log4j2
public class AdvertiserReviewsServiceImpl implements  AdvertiserReviewsService {

    @PersistenceContext
    private EntityManager em;

    private final AdvertiserReviewsMapper advertiserReviewsMapper;

    @Override
    public List<AdvertiserReviewDTO> getAllAdvertiserReviews() {
        List<AdvertiserReviewDTO> advertiserReviewDTOS = advertiserReviewsMapper.getAllAdvertiserReviews();

        if(advertiserReviewDTOS.isEmpty()){
            return Collections.emptyList();
        }

        return advertiserReviewDTOS;
    }

    @Override
    @Transactional
    public Boolean updateAdvertiserReviewVisibility(AdvertiserReviewVisibilityRequestDTO advertiserReviewVisibilityRequestDTO) {
        AdvertiserReview advertiserReview = em.find(AdvertiserReview.class, advertiserReviewVisibilityRequestDTO.getId());

        if(advertiserReview == null){
            return false;
        }

        advertiserReview.setVisibility(advertiserReviewVisibilityRequestDTO.getVisibility());
        em.merge(advertiserReview);

        return true;
    }

    @Override
    public List<AdvertiserReviewDTO> searchAllAdvertiserReviews(AdvertiserReviewSearchRequestDTO advertiserReviewSearchRequestDTO) {
        String keyword = advertiserReviewSearchRequestDTO.getKeyword();
        String searchType = advertiserReviewSearchRequestDTO.getSearchType();
        log.info("keyword:{} searchType:{}", keyword, searchType);
        // 1. 둘 다 비었을 경우 → 전체 조회
        if ((keyword == null || keyword.isBlank()) && (searchType == null || searchType.isBlank())) {
            return advertiserReviewsMapper.getAllAdvertiserReviews();
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
        advertiserReviewSearchRequestDTO.setSearchType(searchType.trim().toLowerCase(Locale.ROOT));
        advertiserReviewSearchRequestDTO.setKeyword(keyword.trim().toLowerCase(Locale.ROOT));
        List<AdvertiserReviewDTO> result = advertiserReviewsMapper.searchAdvertiserReviews(advertiserReviewSearchRequestDTO);
        return result.isEmpty() ? Collections.emptyList() : result;
    }


}

