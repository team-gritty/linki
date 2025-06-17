package com.linki.admin_integration_service.domain.user.controller;

import com.linki.admin_integration_service.domain.user.dto.*;
import com.linki.admin_integration_service.domain.user.service.AdvertiserUserService;
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
public class AdvertiserUserController {

    private final AdvertiserUserService advertiserUserService;
    private final ModelMapper modelMapper;

    @GetMapping("/v1/admin/api/advertiserUsers")
    public ResponseEntity<List<AdvertiserResponseDTO>> getAdvertiserUser(){
        List<AdvertiserUserDTO> advertiserUserDTOS = advertiserUserService.getAllAdvertiserUsers();
        List<AdvertiserResponseDTO> advertiserResponseDTOList =
                advertiserUserDTOS.stream()
                        .map(dto -> modelMapper.map(dto, AdvertiserResponseDTO.class))
                        .toList();

        return ResponseEntity.ok(advertiserResponseDTOList);
    }

    @PostMapping("/v1/admin/api/advertiserUsers/search")
    public ResponseEntity<List<AdvertiserResponseDTO>> searchAdvertiserUser(@RequestBody AdvertiserSearchRequestDTO advertiserSearchRequestDTO){
        List<AdvertiserUserDTO> advertiserUserDTOS = advertiserUserService.searchAdvertiserUser(advertiserSearchRequestDTO);
        List<AdvertiserResponseDTO> advertiserResponseDTOList =
                advertiserUserDTOS.stream()
                        .map(dto -> modelMapper.map(dto,AdvertiserResponseDTO.class))
                        .toList();
        return ResponseEntity.ok(advertiserResponseDTOList);

    }
}
