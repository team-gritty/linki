package com.linki.admin_integration_service.domain.contract.controller;

import com.linki.admin_integration_service.domain.contract.dto.CampaignKeysetResponseDTO;
import com.linki.admin_integration_service.domain.contract.dto.CampaignSearchDTO;
import com.linki.admin_integration_service.domain.contract.service.CampaignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Log4j2
public class CampaignController {

    private final CampaignService campaignService;
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

    @GetMapping("/v1/admin/api/campaigns")
    public ResponseEntity<?> getCampaigns(
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        
        // cursor 정규화: "null" 문자열을 실제 null로 변환
        String normalizedCursor = normalizeCursor(cursor);
        
        CampaignKeysetResponseDTO keysetResponse = campaignService.getCampaignsWithKeyset(normalizedCursor, size);

        return ResponseEntity.ok(keysetResponse);
    }

    @PostMapping("/v1/admin/api/campaigns/search")
    public ResponseEntity<?> searchCampaign(
            @RequestBody CampaignSearchDTO campaignSearchDTO,
            @RequestParam(value = "cursor", required = false) String queryParamCursor,
            @RequestParam(value = "size", defaultValue = "10") int queryParamSize) {
        
        // 요청 바디의 cursor, size가 우선. 없으면 쿼리 파라미터 사용
        String cursor = campaignSearchDTO.getCursor() != null ?
                campaignSearchDTO.getCursor() : queryParamCursor;
        int size = campaignSearchDTO.getSize() != null ?
                campaignSearchDTO.getSize() : queryParamSize;
        
        // cursor 정규화
        String normalizedCursor = normalizeCursor(cursor);
        
        // 새로운 프론트엔드에서는 항상 Keyset 페이지네이션 사용
        CampaignKeysetResponseDTO keysetResponse = 
            campaignService.searchCampaignWithKeyset(campaignSearchDTO, normalizedCursor, size);

        return ResponseEntity.ok(keysetResponse);
    }

    @PostMapping("/v1/admin/api/campaigns/exportExcel")
    public ResponseEntity<String> exportExcel(){
        return ResponseEntity.ok(campaignService.exportExcel());
    }
}
