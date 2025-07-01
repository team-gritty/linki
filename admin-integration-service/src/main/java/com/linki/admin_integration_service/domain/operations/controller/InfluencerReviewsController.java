package com.linki.admin_integration_service.domain.operations.controller;



import com.linki.admin_integration_service.domain.operations.dto.*;
import com.linki.admin_integration_service.domain.operations.service.InfluencerReviewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Log4j2
public class InfluencerReviewsController {

    private final InfluencerReviewsService influencerReviewsService;
    private final ModelMapper modelMapper;

    /**
     * cursor 정규화: "null" 문자열을 실제 null로 변환
     */
    private String normalizeCursor(String cursor) {
        if (cursor == null || cursor.trim().isEmpty() || "null".equalsIgnoreCase(cursor.trim())) {
            return null;
        }
        return cursor.trim();
    }



    @GetMapping("/v1/admin/api/influencerReviews")
    public ResponseEntity<?> getAllInfluencerReviews(@RequestParam(value = "cursor", required = false) String cursor,
                                                                               @RequestParam(value = "size", defaultValue = "10") int size)
    {
        // cursor 정규화: "null" 문자열을 실제 null로 변환
        String normalizedCursor = normalizeCursor(cursor);

        InfluencerReviewKeysetResponseDTO keysetResponseDTO = influencerReviewsService.getAllInfluencerReviewsWithKeyset(normalizedCursor, size);

        return ResponseEntity.ok(keysetResponseDTO);
    }

    @PostMapping("/v1/admin/api/influencerReviews/visibility")
    public ResponseEntity<Boolean> updateInfluencerReviewVisibility(@RequestBody InfluencerReviewVisibilityRequestDTO influencerReviewVisibilityRequestDTO) {
        log.info("Update InfluencerReviewVisibilityRequestDTO");
        log.info(influencerReviewVisibilityRequestDTO);
        Boolean result = influencerReviewsService.updateInfluencerReviewVisibility(influencerReviewVisibilityRequestDTO);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/v1/admin/api/influencerReviews/search")
    public ResponseEntity<?> searchInfluencerReviews(@RequestBody InfluencerReviewSearchRequestDTO influencerReviewSearchRequestDTO,@RequestParam(value = "cursor", required = false) String queryParamCursor,
                                                                               @RequestParam(value = "size", defaultValue = "10") int queryParamSize) {
        // 요청 바디의 cursor, size가 우선. 없으면 쿼리 파라미터 사용
        String cursor = influencerReviewSearchRequestDTO.getCursor() != null ?
                influencerReviewSearchRequestDTO.getCursor() : queryParamCursor;
        int size = influencerReviewSearchRequestDTO.getSize() != null ?
                influencerReviewSearchRequestDTO.getSize() : queryParamSize;

        // cursor 정규화
        String normalizedCursor = normalizeCursor(cursor);

        // 새로운 프론트엔드에서는 항상 Keyset 페이지네이션 사용
        InfluencerReviewKeysetResponseDTO keysetResponse =
                influencerReviewsService.searchAllInfluencerReviewsWithKeyset(influencerReviewSearchRequestDTO, normalizedCursor, size);

        return ResponseEntity.ok(keysetResponse);
    }

    @PostMapping("/v1/admin/api/influencerReviews/exportExcel")
    public ResponseEntity<String> exportExcel(){
        return ResponseEntity.ok(influencerReviewsService.exportExcel());
    }
}
