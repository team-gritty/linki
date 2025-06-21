package com.Gritty.Linki.domain.user.influencer.review.service;

import com.Gritty.Linki.domain.user.influencer.requestDTO.InfAdvertiserReviewRequestDTO;

import java.nio.file.AccessDeniedException;

public interface InfluencerReviewService {

    void submitAdvertiserReview(InfAdvertiserReviewRequestDTO request) throws AccessDeniedException;




}
