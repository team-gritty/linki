package com.linki.admin_integration_service.domain.user.controller;

import com.linki.admin_integration_service.domain.user.dto.*;
import com.linki.admin_integration_service.domain.user.service.AdvertiserUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class AdvertiserUserController {

    private final AdvertiserUserService advertiserUserService;

    /**
     * cursor 정규화: "null" 문자열을 실제 null로 변환
     */
    private String normalizeCursor(String cursor) {
        if (cursor == null || cursor.trim().isEmpty() || "null".equalsIgnoreCase(cursor.trim())) {
            return null;
        }
        return cursor.trim();
    }

    @GetMapping("/v1/admin/api/advertiserUsers")
    public ResponseEntity<?> getAdvertiserUser(@RequestParam(value = "cursor", required = false) String cursor,
                                                                         @RequestParam(value = "size", defaultValue = "10") int size){
        // cursor 정규화: "null" 문자열을 실제 null로 변환
        String normalizedCursor = normalizeCursor(cursor);

        AdvertiserKeysetResponseDTO keysetResponseDTO = advertiserUserService.getAllAdvertiserUsersWithKeyset(normalizedCursor, size);

        return ResponseEntity.ok(keysetResponseDTO);
    }

    @PostMapping("/v1/admin/api/advertiserUsers/search")
    public ResponseEntity<?> searchAdvertiserUser(@RequestBody AdvertiserSearchRequestDTO advertiserSearchRequestDTO,@RequestParam(value = "cursor", required = false) String queryParamCursor,
                                                                            @RequestParam(value = "size", defaultValue = "10") int queryParamSize) {
        // 요청 바디의 cursor, size가 우선. 없으면 쿼리 파라미터 사용
        String cursor = advertiserSearchRequestDTO.getCursor() != null ?
                advertiserSearchRequestDTO.getCursor() : queryParamCursor;
        int size = advertiserSearchRequestDTO.getSize() != null ?
                advertiserSearchRequestDTO.getSize() : queryParamSize;

        // cursor 정규화
        String normalizedCursor = normalizeCursor(cursor);

        // 새로운 프론트엔드에서는 항상 Keyset 페이지네이션 사용
        AdvertiserKeysetResponseDTO keysetResponse =
                advertiserUserService.searchAdvertiserUserWithKeyset(advertiserSearchRequestDTO, normalizedCursor, size);

        return ResponseEntity.ok(keysetResponse);
    }

    @PostMapping("/v1/admin/api/advertiserUsers/exportExcel")
    public ResponseEntity<String> exportExcel(){
        return ResponseEntity.ok(advertiserUserService.exportExcel());
    }
}
