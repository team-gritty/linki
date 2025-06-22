package com.linki.admin_integration_service.domain.operations.controller;



import com.linki.admin_integration_service.domain.operations.dto.InfluencerResponseDTO;
import com.linki.admin_integration_service.domain.operations.dto.InfluencerReviewDTO;
import com.linki.admin_integration_service.domain.operations.dto.InfluencerReviewSearchRequestDTO;
import com.linki.admin_integration_service.domain.operations.dto.InfluencerReviewVisibilityRequestDTO;
import com.linki.admin_integration_service.domain.operations.service.InfluencerReviewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
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

    @PostMapping("/v1/admin/api/influencerReviews/visibility")
    public ResponseEntity<Boolean> updateInfluencerReviewVisibility(@RequestBody InfluencerReviewVisibilityRequestDTO influencerReviewVisibilityRequestDTO) {
        log.info("Update InfluencerReviewVisibilityRequestDTO");
        log.info(influencerReviewVisibilityRequestDTO);
        Boolean result = influencerReviewsService.updateInfluencerReviewVisibility(influencerReviewVisibilityRequestDTO);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/v1/admin/api/influencerReviews/search")
    public ResponseEntity<List<InfluencerResponseDTO>> searchInfluencerReviews(@RequestBody InfluencerReviewSearchRequestDTO influencerReviewSearchRequestDTO) {
        List<InfluencerReviewDTO> influencerReviewDTOList = influencerReviewsService.searchAllInfluencerReviews(influencerReviewSearchRequestDTO);
        List<InfluencerResponseDTO> influencerResponseDTOList = influencerReviewDTOList.stream()
                .map(dto -> modelMapper.map(dto, InfluencerResponseDTO.class))
                .toList();
        return ResponseEntity.ok(influencerResponseDTOList);
    }

    @PostMapping("/v1/admin/api/influencerReviews/exportExcel")
    public ResponseEntity<String> exportExcel(){
        return ResponseEntity.ok(influencerReviewsService.exportExcel());
    }
}
