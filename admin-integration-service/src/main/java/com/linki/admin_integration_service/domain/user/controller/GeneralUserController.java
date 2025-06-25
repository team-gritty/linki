package com.linki.admin_integration_service.domain.user.controller;

import com.linki.admin_integration_service.domain.user.dto.GeneralUserKeysetResponseDTO;
import com.linki.admin_integration_service.domain.user.dto.GeneralUserSearchRequestDTO;
import com.linki.admin_integration_service.domain.user.service.GeneralUserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class GeneralUserController {

    private final GeneralUserService generalUserService;
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


    @GetMapping("/v1/admin/api/generalUsers")
    public ResponseEntity<?> getGeneralUser(@RequestParam(value = "cursor", required = false) String cursor,
                                                                       @RequestParam(value = "size", defaultValue = "10") int size){
        // cursor 정규화: "null" 문자열을 실제 null로 변환
        String normalizedCursor = normalizeCursor(cursor);

        GeneralUserKeysetResponseDTO keysetResponseDTO = generalUserService.getAllGeneralUsersWithKeyset(normalizedCursor, size);

        return ResponseEntity.ok(keysetResponseDTO);
    }


    @PostMapping("/v1/admin/api/generalUsers/search")
    public ResponseEntity<?> searchGeneralUser(@RequestBody GeneralUserSearchRequestDTO generalUserSearchRequestDTO,@RequestParam(value = "cursor", required = false) String queryParamCursor,
                                                                          @RequestParam(value = "size", defaultValue = "10") int queryParamSize) {
        // 요청 바디의 cursor, size가 우선. 없으면 쿼리 파라미터 사용
        String cursor = generalUserSearchRequestDTO.getCursor() != null ?
                generalUserSearchRequestDTO.getCursor() : queryParamCursor;
        int size = generalUserSearchRequestDTO.getSize() != null ?
                generalUserSearchRequestDTO.getSize() : queryParamSize;

        // cursor 정규화
        String normalizedCursor = normalizeCursor(cursor);

        // 새로운 프론트엔드에서는 항상 Keyset 페이지네이션 사용
        GeneralUserKeysetResponseDTO keysetResponse =
                generalUserService.searchGeneralUserWithKeyset(generalUserSearchRequestDTO, normalizedCursor, size);

        return ResponseEntity.ok(keysetResponse);
    }

    @PostMapping("/v1/admin/api/generalUsers/exportExcel")
    public ResponseEntity<String> exportExcel(){
        return ResponseEntity.ok(generalUserService.exportExcel());
    }
}
