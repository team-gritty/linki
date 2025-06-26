package com.linki.admin_integration_service.domain.contract.controller;

import com.linki.admin_integration_service.domain.contract.dto.*;
import com.linki.admin_integration_service.domain.contract.service.ContractService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Log4j2
public class ContractController {


    private final ContractService contractService;
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

    @GetMapping("/v1/admin/api/contracts")
    public ResponseEntity<?> getContracts(@RequestParam(value = "cursor", required = false) String cursor,
                                                                  @RequestParam(value = "size", defaultValue = "10") int size){
        // cursor 정규화: "null" 문자열을 실제 null로 변환
        String normalizedCursor = normalizeCursor(cursor);

        ContractKeysetResponseDTO keysetResponseDTO = contractService.getContractsWithKeyset(normalizedCursor, size);

        return ResponseEntity.ok(keysetResponseDTO);
    }

    @PostMapping("/v1/admin/api/contracts/search")
    public ResponseEntity<?> searchContract(@RequestBody ContractSearchDTO contractSearchDTO,
                                                                    @RequestParam(value = "cursor", required = false) String queryParamCursor,
                                                                    @RequestParam(value = "size", defaultValue = "10") int queryParamSize){
        // 요청 바디의 cursor, size가 우선. 없으면 쿼리 파라미터 사용
        String cursor = contractSearchDTO.getCursor() != null ?
                contractSearchDTO.getCursor() : queryParamCursor;
        int size = contractSearchDTO.getSize() != null ?
                contractSearchDTO.getSize() : queryParamSize;

        // cursor 정규화
        String normalizedCursor = normalizeCursor(cursor);

        // 새로운 프론트엔드에서는 항상 Keyset 페이지네이션 사용
        ContractKeysetResponseDTO keysetResponse =
                contractService.searchContractsWithKeyset(contractSearchDTO, normalizedCursor, size);

        return ResponseEntity.ok(keysetResponse);

    }

    @PostMapping("/v1/admin/api/contracts/exportExcel")
    public ResponseEntity<String> exportExcel(){
        log.info("exportExcel");
        return ResponseEntity.ok(contractService.exportExcel());
    }
}
