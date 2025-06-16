package com.linki.admin_integration_service.domain.operations.repository;

import com.linki.admin_integration_service.domain.operations.dto.InfluencerReviewDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InfluencerReviewsMapper {
    List<InfluencerReviewDTO> getAllInfluencerReviews();
}
