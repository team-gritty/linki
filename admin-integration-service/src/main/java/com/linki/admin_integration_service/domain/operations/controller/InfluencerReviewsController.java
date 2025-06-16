package com.linki.admin_integration_service.domain.operations.controller;



import com.linki.admin_integration_service.domain.operations.dto.InfluencerResponseDTO;
import com.linki.admin_integration_service.domain.operations.dto.InfluencerReviewDTO;
import com.linki.admin_integration_service.domain.operations.service.InfluencerReviewsService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InfluencerReviewsController {

    private final InfluencerReviewsService influencerReviewsService;
    private final ModelMapper modelMapper;

    @GetMapping("/v1/admin/api/influencerReviews")
    public ResponseEntity<List<InfluencerResponseDTO>> getAllInfluencerReviews() {
        List<InfluencerReviewDTO> influencerReviewDTOList = influencerReviewsService.getAllInfluencerReviews();
        List<InfluencerResponseDTO> influencerResponseDTOList = influencerReviewDTOList.stream()
            .map(dto -> modelMapper.map(dto, InfluencerResponseDTO.class))
            .toList();
        return ResponseEntity.ok(influencerResponseDTOList);
    }
}
