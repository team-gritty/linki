package com.linki.admin_integration_service.domain.operations.service;

import com.linki.admin_integration_service.domain.operations.dto.*;


import java.util.List;


public interface InfluencerReviewsService {

    List<InfluencerReviewDTO> getAllInfluencerReviews();
    Boolean updateInfluencerReviewVisibility(InfluencerReviewVisibilityRequestDTO influencerReviewVisibilityRequestDTO);
    List<InfluencerReviewDTO> searchAllInfluencerReviews(InfluencerReviewSearchRequestDTO influencerReviewSearchRequestDTO);
    String exportExcel();


    InfluencerReviewKeysetResponseDTO getAllInfluencerReviewsWithKeyset(String cursor, int size);
    InfluencerReviewKeysetResponseDTO searchAllInfluencerReviewsWithKeyset(InfluencerReviewSearchRequestDTO influencerReviewSearchRequestDTO, String cursor, int size);
}
