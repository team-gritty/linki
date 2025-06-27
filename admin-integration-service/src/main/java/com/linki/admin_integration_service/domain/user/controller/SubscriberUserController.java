package com.linki.admin_integration_service.domain.user.controller;


import com.linki.admin_integration_service.domain.user.dto.*;
import com.linki.admin_integration_service.domain.user.service.SubscriberUserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SubscriberUserController {

    private final SubscriberUserService subscriberUserService;
    private final ModelMapper modelMapper;

    @GetMapping("/v1/admin/api/subscriberUsers")
    public ResponseEntity<SubscriberKeysetResponseDTO> getSubscriberUser(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int size) {
        
        SubscriberKeysetResponseDTO response = subscriberUserService.getAllSubscriberUsersWithKeyset(cursor, size);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/v1/admin/api/subscriberUsers/search")
    public ResponseEntity<Object> searchSubscriberUser(@RequestBody SubscriberSearchRequestDTO searchRequest) {
        // Keyset 페이지네이션 검색
        if (searchRequest.getCursor() != null || searchRequest.getSize() != null) {
            String cursor = searchRequest.getCursor();
            int size = searchRequest.getSize() != null ? searchRequest.getSize() : 10;
            SubscriberKeysetResponseDTO response = subscriberUserService.searchSubscriberUserWithKeyset(searchRequest, cursor, size);
            return ResponseEntity.ok(response);
        }
        
        // 기존 검색 (하위 호환성)
        List<SubscriberUserDTO> subscriberUserDTOList = subscriberUserService.searchSubscriberUser(searchRequest);
        List<SubscriberUserResponseDTO> subscriberUserResponseDTOS =
                subscriberUserDTOList.stream()
                        .map(dto -> modelMapper.map(dto,SubscriberUserResponseDTO.class))
                        .toList();
        return ResponseEntity.ok(subscriberUserResponseDTOS);
    }

    @PostMapping("/v1/admin/api/subscriberUsers/exportExcel")
    public ResponseEntity<String> exportExcel(){
        return ResponseEntity.ok(subscriberUserService.exportExcel());
    }
}
