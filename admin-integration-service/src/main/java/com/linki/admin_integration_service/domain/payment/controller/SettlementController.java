package com.linki.admin_integration_service.domain.payment.controller;

import com.linki.admin_integration_service.domain.payment.dto.SettlementDTO;
import com.linki.admin_integration_service.domain.payment.dto.SettlementRequestDTO;
import com.linki.admin_integration_service.domain.payment.dto.SettlementResponseDTO;
import com.linki.admin_integration_service.domain.payment.dto.SettlementSearchDTO;
import com.linki.admin_integration_service.domain.payment.service.SettlementService;
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
public class SettlementController {

    private final SettlementService settlementService;
    private final ModelMapper modelMapper;

    @GetMapping("/v1/admin/api/settlements")
    public ResponseEntity<List<SettlementResponseDTO>> getSettlements(){
        List<SettlementDTO> settlementDTOS = settlementService.getAllSettlements();
        List<SettlementResponseDTO> settlementResponseDTOS =
                settlementDTOS.stream()
                        .map(dto -> modelMapper.map(dto, SettlementResponseDTO.class))
                        .toList();

        return ResponseEntity.ok(settlementResponseDTOS);
    }

    @PostMapping("/v1/admin/api/settlements/search")
    public ResponseEntity<List<SettlementResponseDTO>> searchAdvertiserUser(@RequestBody SettlementSearchDTO settlementSearchDTO){
        List<SettlementDTO> settlementDTOS = settlementService.searchSettlement(settlementSearchDTO);
        List<SettlementResponseDTO> settlementResponseDTOS =
                settlementDTOS.stream()
                        .map(dto -> modelMapper.map(dto, SettlementResponseDTO.class))
                        .toList();
        return ResponseEntity.ok(settlementResponseDTOS);

    }


    @PostMapping("/v1/admin/api/settlements/process")
    public ResponseEntity<String> approveSettlement(@RequestBody SettlementRequestDTO settlementRequestDTO){

        if (!settlementService.approveSettlement(settlementRequestDTO)){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok("OK");
    }
}
