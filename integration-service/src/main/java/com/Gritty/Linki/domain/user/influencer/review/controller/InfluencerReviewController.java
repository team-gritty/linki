package com.Gritty.Linki.domain.user.influencer.review.controller;

import com.Gritty.Linki.domain.user.influencer.requestDTO.InfAdvertiserReviewRequestDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.review.InfAdvertiserReviewResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.review.ReceivedInfluencerReviewResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.review.ReviewableContractResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.review.WrittenAdvertiserReviewResponseDTO;
import com.Gritty.Linki.domain.user.influencer.review.service.InfluencerReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
public class InfluencerReviewController  {
    private final InfluencerReviewService influencerReviewService;

    @PostMapping("/v1/api/influencer/mypage/reviews/advertiser-write")
    public ResponseEntity<String> submitAdvertiserReview(@Valid @RequestBody InfAdvertiserReviewRequestDTO requestDTO) throws AccessDeniedException {
        try {
            influencerReviewService.submitAdvertiserReview(requestDTO);
            return ResponseEntity.ok("리뷰가 성공적으로 등록되었습니다");
        } catch (IllegalStateException e) {
            log.warn("리뷰 작성 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("리뷰 작성 중 예상치 못한 오류 발생", e);
            return ResponseEntity.internalServerError().body("리뷰 작성 중 오류가 발생했습니다.");
        }
    }

    @GetMapping("/v1/api/influencer/mypage/reviews/available-contracts")
    public ResponseEntity<List<ReviewableContractResponseDTO>> getAvailableContracts() {
        List<ReviewableContractResponseDTO> contracts =  influencerReviewService.getReviewableContracts();
        return ResponseEntity.ok(contracts);

    }

    @GetMapping("/v1/api/nonuser/reviews/advertiser/campaign/{campaignId}")
    public ResponseEntity<List<InfAdvertiserReviewResponseDTO>>getAdvertiserReviewsByCampaign(@PathVariable String campaignId){
      List<InfAdvertiserReviewResponseDTO>reviews = influencerReviewService.getAdvertiseReviewsByCampaign(campaignId);
      return ResponseEntity.ok(reviews);
    }

    @GetMapping("/v1/api/influencer/mypage/reviews/received")
    public ResponseEntity<List<ReceivedInfluencerReviewResponseDTO>> getReceivedAdvertiserReviews() {
        List<ReceivedInfluencerReviewResponseDTO> reviews = influencerReviewService.getReceivedInfluencerReviews();
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/v1/api/influencer/mypage/reviews/written")
    public ResponseEntity<List<WrittenAdvertiserReviewResponseDTO>> getWrittenAdvertiserReviews() {
        List<WrittenAdvertiserReviewResponseDTO> reviews = influencerReviewService.getWrittenAdvertiserReviews();
        return ResponseEntity.ok(reviews);
    }
}
