package com.linki.admin_integration_service.domain.operations.controller;

import com.linki.admin_integration_service.domain.operations.dto.*;
import com.linki.admin_integration_service.domain.operations.service.AdminSignUpAgreementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequiredArgsConstructor
@Log4j2
public class AdminSignUpAgreementController {

    private final AdminSignUpAgreementService adminSignUpAgreementService;

    /**
     * cursor 정규화: "null" 문자열을 실제 null로 변환
     */
    private String normalizeCursor(String cursor) {
        if (cursor == null || cursor.trim().isEmpty() || "null".equalsIgnoreCase(cursor.trim())) {
            return null;
        }
        return cursor.trim();
    }

    @GetMapping("/v1/admin/api/adminSignUp")
    public ResponseEntity<?> getAdminSignUpList(@RequestParam(value = "cursor", required = false) String cursor,
                                                @RequestParam(value = "size", defaultValue = "10") int size){
        // cursor 정규화: "null" 문자열을 실제 null로 변환
        String normalizedCursor = normalizeCursor(cursor);

        AdminSignUpKeysetResponseDTO keysetResponseDTO = adminSignUpAgreementService.getAdminSignUpListWithKeyset(normalizedCursor, size);

        return ResponseEntity.ok(keysetResponseDTO);
    }

    @PostMapping("/v1/admin/api/adminSignUp/approve")
    public ResponseEntity<String> approveAdminSignUp(@RequestBody AdminSignUpAgreementRequestDTO adminSignUpAgreementRequestDTO){
        adminSignUpAgreementService.approveAdminSignUp(adminSignUpAgreementRequestDTO);
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/v1/admin/api/adminSignUp/reject")
    public ResponseEntity<String> rejectAdminSignUp(@RequestBody AdminSignUpAgreementRequestDTO adminSignUpAgreementRequestDTO){
        adminSignUpAgreementService.rejectAdminSignUp(adminSignUpAgreementRequestDTO);
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/v1/admin/api/adminSignUp/search")
    public ResponseEntity<?> searchAdminSignUp(@RequestBody AdminSignUpAgreementSearchRequestDTO advertiserReviewSearchRequestDTO,@RequestParam(value = "cursor", required = false) String queryParamCursor,
                                                                                   @RequestParam(value = "size", defaultValue = "10") int queryParamSize) {
        // 요청 바디의 cursor, size가 우선. 없으면 쿼리 파라미터 사용
        String cursor = advertiserReviewSearchRequestDTO.getCursor() != null ?
                advertiserReviewSearchRequestDTO.getCursor() : queryParamCursor;
        int size = advertiserReviewSearchRequestDTO.getSize() != null ?
                advertiserReviewSearchRequestDTO.getSize() : queryParamSize;

        // cursor 정규화
        String normalizedCursor = normalizeCursor(cursor);

        // 새로운 프론트엔드에서는 항상 Keyset 페이지네이션 사용
        AdminSignUpKeysetResponseDTO keysetResponse =
                adminSignUpAgreementService.searchAdminSignUpWithKeyset(advertiserReviewSearchRequestDTO, normalizedCursor, size);

        return ResponseEntity.ok(keysetResponse);
    }

    @PostMapping("/v1/admin/api/adminSignUp/exportExcel")
    public ResponseEntity<String> exportExcel(){
        return ResponseEntity.ok(adminSignUpAgreementService.exportExcel());
    }
}
