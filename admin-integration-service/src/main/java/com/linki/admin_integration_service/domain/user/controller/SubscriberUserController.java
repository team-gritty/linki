package com.linki.admin_integration_service.domain.user.controller;


import com.linki.admin_integration_service.domain.user.dto.*;
import com.linki.admin_integration_service.domain.user.service.SubscriberUserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SubscriberUserController {

    private final SubscriberUserService subscriberUserService;
    private final ModelMapper modelMapper;

    @GetMapping("/v1/admin/api/subscriberUsers")
    public ResponseEntity<List<SubscriberUserResponseDTO>> getSubscriberUser(){
        List<SubscriberUserDTO> subscriberUserDTOList = subscriberUserService.getAllSubscriberUsers();
        List<SubscriberUserResponseDTO> subscriberUserResponseDTOS =
                subscriberUserDTOList.stream()
                        .map(dto -> modelMapper.map(dto, SubscriberUserResponseDTO.class))
                        .toList();

        return ResponseEntity.ok(subscriberUserResponseDTOS);
    }


    @PostMapping("/v1/admin/api/subscriberUsers/search")
    public ResponseEntity<List<SubscriberUserResponseDTO>> searchSubscriberUser(@RequestBody SubscriberSearchRequestDTO subscriberSearchRequestDTO){
        List<SubscriberUserDTO> subscriberUserDTOList = subscriberUserService.searchSubscriberUser(subscriberSearchRequestDTO);
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
