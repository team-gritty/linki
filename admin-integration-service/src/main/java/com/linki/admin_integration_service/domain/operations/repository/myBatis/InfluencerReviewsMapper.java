package com.linki.admin_integration_service.domain.operations.repository.myBatis;

import com.linki.admin_integration_service.domain.operations.dto.InfluencerReviewDTO;
import com.linki.admin_integration_service.domain.operations.dto.InfluencerReviewSearchRequestDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InfluencerReviewsMapper {
    List<InfluencerReviewDTO> getAllInfluencerReviews();
    List<InfluencerReviewDTO> searchInfluencerReviews(InfluencerReviewSearchRequestDTO influencerReviewSearchRequestDTO);
}
