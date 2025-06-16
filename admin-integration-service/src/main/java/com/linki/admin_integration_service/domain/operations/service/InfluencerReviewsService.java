package com.linki.admin_integration_service.domain.operations.service;

import com.linki.admin_integration_service.domain.operations.dto.InfluencerReviewDTO;
import com.linki.admin_integration_service.domain.operations.dto.InfluencerReviewSearchRequestDTO;
import com.linki.admin_integration_service.domain.operations.dto.InfluencerReviewVisibilityRequestDTO;


import java.util.List;


public interface InfluencerReviewsService {

    List<InfluencerReviewDTO> getAllInfluencerReviews();
    Boolean updateInfluencerReviewVisibility(InfluencerReviewVisibilityRequestDTO influencerReviewVisibilityRequestDTO);
    List<InfluencerReviewDTO> searchAllInfluencerReviews(InfluencerReviewSearchRequestDTO influencerReviewSearchRequestDTO);

}
