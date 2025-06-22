package com.linki.admin_integration_service.domain.operations.controller;

import com.linki.admin_integration_service.domain.operations.dto.*;
import com.linki.admin_integration_service.domain.operations.service.AdminSignUpAgreementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Log4j2
public class AdminSignUpAgreementController {

    private final AdminSignUpAgreementService adminSignUpAgreementService;

    private final ModelMapper modelMapper;

    @GetMapping("/v1/admin/api/adminSignUp")
    public ResponseEntity<List<AdminSignUpAgreementResponseDTO>> getAdminSignUpList(){
        List<AdminSignUpAgreementDTO> adminSignUpAgreementDTOList = adminSignUpAgreementService.getAdminSignUpList();
        List<AdminSignUpAgreementResponseDTO> adminSignUpAgreementResponseDTOList =
                adminSignUpAgreementDTOList.stream().
                map(dto -> modelMapper.map(dto, AdminSignUpAgreementResponseDTO.class))
                        .toList();


        return ResponseEntity.ok(adminSignUpAgreementResponseDTOList);
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
    public ResponseEntity<List<AdminSignUpAgreementResponseDTO>> searchAdminSignUp(@RequestBody AdminSignUpAgreementSearchRequestDTO advertiserReviewSearchRequestDTO) {
        List<AdminSignUpAgreementDTO> adminSignUpAgreementDTOS = adminSignUpAgreementService.searchAdminSignUp(advertiserReviewSearchRequestDTO);
        List<AdminSignUpAgreementResponseDTO> adminSignUpAgreementResponseDTOList = adminSignUpAgreementDTOS.stream()
                .map(dto -> modelMapper.map(dto, AdminSignUpAgreementResponseDTO.class))
                .toList();
        return ResponseEntity.ok(adminSignUpAgreementResponseDTOList);
    }

    @PostMapping("/v1/admin/api/adminSignUp/exportExcel")
    public ResponseEntity<String> exportExcel(){
        return ResponseEntity.ok(adminSignUpAgreementService.exportExcel());
    }
}
