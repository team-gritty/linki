package com.linki.admin_integration_service.domain.operations.service;

import com.linki.admin_integration_service.domain.operations.dto.*;

import java.util.List;


public interface AdvertiserReviewsService {

    List<AdvertiserReviewDTO> getAllAdvertiserReviews();
    Boolean updateAdvertiserReviewVisibility(AdvertiserReviewVisibilityRequestDTO advertiserReviewVisibilityRequestDTO);
    List<AdvertiserReviewDTO> searchAllAdvertiserReviews(AdvertiserReviewSearchRequestDTO advertiserReviewSearchRequestDTO);
    String exportExcel();

}
