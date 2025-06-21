package com.Gritty.Linki.domain.user.influencer.review.service;

import com.Gritty.Linki.domain.user.influencer.requestDTO.InfAdvertiserReviewRequestDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.ReviewableContractResponseDTO;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface InfluencerReviewService {

    void submitAdvertiserReview(InfAdvertiserReviewRequestDTO request) throws AccessDeniedException;
    List<ReviewableContractResponseDTO> getReviewableContracts();





}
