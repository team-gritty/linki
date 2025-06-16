package com.linki.admin_integration_service.domain.operations.repository.myBatis;

import com.linki.admin_integration_service.domain.operations.dto.AdvertiserReviewDTO;
import com.linki.admin_integration_service.domain.operations.dto.AdvertiserReviewSearchRequestDTO;

import java.util.List;

public interface AdvertiserReviewsMapper {
    List<AdvertiserReviewDTO> getAllAdvertiserReviews();
    List<AdvertiserReviewDTO> searchAdvertiserReviews(AdvertiserReviewSearchRequestDTO advertiserReviewSearchRequestDTO);
}
