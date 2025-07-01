package com.linki.admin_integration_service.domain.payment.controller;

import com.linki.admin_integration_service.domain.payment.dto.*;
import com.linki.admin_integration_service.domain.payment.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;
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

    @GetMapping("/v1/admin/api/settlements")
    public ResponseEntity<?> getSettlements(@RequestParam(value = "cursor", required = false) String cursor,
                                            @RequestParam(value = "size", defaultValue = "10") int size){
        // cursor 정규화: "null" 문자열을 실제 null로 변환
        String normalizedCursor = normalizeCursor(cursor);

        SettlementKeysetResponseDTO keysetResponseDTO = settlementService.getAllSettlementsWithKeyset(normalizedCursor, size);

        return ResponseEntity.ok(keysetResponseDTO);
    }

    @PostMapping("/v1/admin/api/settlements/search")
    public ResponseEntity<?> searchAdvertiserUser(@RequestBody SettlementSearchDTO settlementSearchDTO,@RequestParam(value = "cursor", required = false) String queryParamCursor,
                                                                            @RequestParam(value = "size", defaultValue = "10") int queryParamSize) {
        // 요청 바디의 cursor, size가 우선. 없으면 쿼리 파라미터 사용
        String cursor = settlementSearchDTO.getCursor() != null ?
                settlementSearchDTO.getCursor() : queryParamCursor;
        int size = settlementSearchDTO.getSize() != null ?
                settlementSearchDTO.getSize() : queryParamSize;

        // cursor 정규화
        String normalizedCursor = normalizeCursor(cursor);

        // 새로운 프론트엔드에서는 항상 Keyset 페이지네이션 사용
        SettlementKeysetResponseDTO keysetResponse =
                settlementService.searchSettlementWithKeyset(settlementSearchDTO, normalizedCursor, size);

        return ResponseEntity.ok(keysetResponse);
    }



    @PostMapping("/v1/admin/api/settlements/process")
    public ResponseEntity<String> approveSettlement(@RequestBody SettlementRequestDTO settlementRequestDTO){

        if (!settlementService.approveSettlement(settlementRequestDTO)){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok("OK");
    }

    @PostMapping("/v1/admin/api/settlements/exportExcel")
    public ResponseEntity<String> exportExcel(){
        return ResponseEntity.ok(settlementService.exportExcel());
    }
}
