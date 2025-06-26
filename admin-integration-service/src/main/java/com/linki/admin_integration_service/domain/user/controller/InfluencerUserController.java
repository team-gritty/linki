package com.linki.admin_integration_service.domain.user.controller;

import com.linki.admin_integration_service.domain.user.dto.*;
import com.linki.admin_integration_service.domain.user.service.InfluencerUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class InfluencerUserController {

    private final InfluencerUserService influencerUserService;

    /**
     * cursor 정규화: "null" 문자열을 실제 null로 변환
     */
    private String normalizeCursor(String cursor) {
        if (cursor == null || cursor.trim().isEmpty() || "null".equalsIgnoreCase(cursor.trim())) {
            return null;
        }
        return cursor.trim();
    }


    @GetMapping("/v1/admin/api/influencerUsers")
    public ResponseEntity<InfluencerKeysetResponseDTO> getInfluencerUser(@RequestParam(value = "cursor", required = false) String cursor,
                                                                         @RequestParam(value = "size", defaultValue = "10") int size){
        // cursor 정규화: "null" 문자열을 실제 null로 변환
        String normalizedCursor = normalizeCursor(cursor);

        InfluencerKeysetResponseDTO keysetResponseDTO = influencerUserService.getAllInfluencerUsersWithKeyset(normalizedCursor, size);

        return ResponseEntity.ok(keysetResponseDTO);
    }

    @PostMapping("/v1/admin/api/influencerUsers/search")
    public ResponseEntity<?> searchInfluencerUser(@RequestBody InfluencerUserSearchRequestDTO searchRequest,@RequestParam(value = "cursor", required = false) String queryParamCursor,
                                                  @RequestParam(value = "size", defaultValue = "10") int queryParamSize) {
        // 요청 바디의 cursor, size가 우선. 없으면 쿼리 파라미터 사용
        String cursor = searchRequest.getCursor() != null ?
                searchRequest.getCursor() : queryParamCursor;
        int size = searchRequest.getSize() != null ?
                searchRequest.getSize() : queryParamSize;

        // cursor 정규화
        String normalizedCursor = normalizeCursor(cursor);

        // 새로운 프론트엔드에서는 항상 Keyset 페이지네이션 사용
        InfluencerKeysetResponseDTO keysetResponse =
                influencerUserService.searchInfluencerUserWithKeyset(searchRequest, normalizedCursor, size);

        return ResponseEntity.ok(keysetResponse);
    }

    @PostMapping("/v1/admin/api/influencerUsers/exportExcel")
    public ResponseEntity<String> exportExcel(){
        return ResponseEntity.ok(influencerUserService.exportExcel());
    }
}
