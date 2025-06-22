package com.linki.admin_integration_service.domain.contract.controller;

import com.linki.admin_integration_service.domain.contract.dto.ContractDTO;
import com.linki.admin_integration_service.domain.contract.dto.ContractResponseDTO;
import com.linki.admin_integration_service.domain.contract.dto.ContractSearchDTO;
import com.linki.admin_integration_service.domain.contract.service.ContractService;
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
public class ContractController {


    private final ContractService contractService;
    private final ModelMapper modelMapper;

    @GetMapping("/v1/admin/api/contracts")
    public ResponseEntity<List<ContractResponseDTO>> getContracts(){
        List<ContractDTO> contractDTOS = contractService.getContracts();
        List<ContractResponseDTO> contractResponseDTOList =
                contractDTOS.stream()
                        .map(dto -> modelMapper.map(dto, ContractResponseDTO.class))
                        .toList();

        return ResponseEntity.ok(contractResponseDTOList);
    }

    @PostMapping("/v1/admin/api/contracts/search")
    public ResponseEntity<List<ContractResponseDTO>> searchContract(@RequestBody ContractSearchDTO contractSearchDTO){
        List<ContractDTO> contractDTOS = contractService.searchContract(contractSearchDTO);
        List<ContractResponseDTO> contractResponseDTOList =
                contractDTOS.stream()
                        .map(dto -> modelMapper.map(dto, ContractResponseDTO.class))
                        .toList();
        return ResponseEntity.ok(contractResponseDTOList);

    }

    @PostMapping("/v1/admin/api/contracts/exportExcel")
    public ResponseEntity<String> exportExcel(){
        return ResponseEntity.ok(contractService.exportExcel());
    }
}
