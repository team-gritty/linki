package com.linki.admin_integration_service.domain.user.controller;

import com.linki.admin_integration_service.domain.user.dto.*;
import com.linki.admin_integration_service.domain.user.service.InfluencerUserService;
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
public class InfluencerUserController {

    private final InfluencerUserService influencerUserService;
    private final ModelMapper modelMapper;

    @GetMapping("/v1/admin/api/influencerUsers")
    public ResponseEntity<List<InfluencerUserResponseDTO>> getInfluencerUser(){
        List<InfluencerUserDTO> advertiserUserDTOS = influencerUserService.getAllInfluencerUsers();
        List<InfluencerUserResponseDTO> influencerUserResponseDTOList =
                advertiserUserDTOS.stream()
                        .map(dto -> modelMapper.map(dto, InfluencerUserResponseDTO.class))
                        .toList();

        return ResponseEntity.ok(influencerUserResponseDTOList);
    }

    @PostMapping("/v1/admin/api/influencerUsers/search")
    public ResponseEntity<List<InfluencerUserResponseDTO>> searchInfluencerUser(@RequestBody InfluencerUserSearchRequestDTO influencerUserSearchRequestDTO){
        List<InfluencerUserDTO> advertiserUserDTOS = influencerUserService.searchInfluencerUser(influencerUserSearchRequestDTO);
        List<InfluencerUserResponseDTO> influencerUserResponseDTOList =
                advertiserUserDTOS.stream()
                        .map(dto -> modelMapper.map(dto,InfluencerUserResponseDTO.class))
                        .toList();
        return ResponseEntity.ok(influencerUserResponseDTOList);

    }
}
