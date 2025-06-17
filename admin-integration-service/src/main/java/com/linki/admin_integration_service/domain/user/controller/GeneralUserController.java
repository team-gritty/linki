package com.linki.admin_integration_service.domain.user.controller;

import com.linki.admin_integration_service.domain.user.dto.GeneralUSerDTO;
import com.linki.admin_integration_service.domain.user.dto.GeneralUserResponseDTO;
import com.linki.admin_integration_service.domain.user.dto.GeneralUserSearchRequestDTO;
import com.linki.admin_integration_service.domain.user.service.GeneralUserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GeneralUserController {

    private final GeneralUserService generalUserService;
    private final ModelMapper modelMapper;

    @GetMapping("/v1/admin/api/generalUsers")
    public ResponseEntity<List<GeneralUserResponseDTO>> getGeneralUser(){
        List<GeneralUSerDTO> generalUSerDTOList = generalUserService.getAllGeneralUsers();
        List<GeneralUserResponseDTO> generalUserResponseDTOS =
                generalUSerDTOList.stream()
                        .map(dto -> modelMapper.map(dto,GeneralUserResponseDTO.class))
                        .toList();

        return ResponseEntity.ok(generalUserResponseDTOS);
    }


    @PostMapping("/v1/admin/api/generalUsers/search")
    public ResponseEntity<List<GeneralUserResponseDTO>> searchGeneralUser(@RequestBody GeneralUserSearchRequestDTO generalUserSearchRequestDTO){
        List<GeneralUSerDTO> generalUSerDTOList = generalUserService.searchGeneralUser(generalUserSearchRequestDTO);
        List<GeneralUserResponseDTO> generalUserResponseDTOS =
                generalUSerDTOList.stream()
                        .map(dto -> modelMapper.map(dto,GeneralUserResponseDTO.class))
                        .toList();
        return ResponseEntity.ok(generalUserResponseDTOS);

    }
}
