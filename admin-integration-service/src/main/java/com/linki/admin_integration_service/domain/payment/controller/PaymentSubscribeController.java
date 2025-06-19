package com.linki.admin_integration_service.domain.payment.controller;

import com.linki.admin_integration_service.domain.payment.dto.PaymentSubscribeDTO;
import com.linki.admin_integration_service.domain.payment.dto.PaymentSubscribeResponseDTO;
import com.linki.admin_integration_service.domain.payment.dto.PaymentSubscribeSearchDTO;
import com.linki.admin_integration_service.domain.payment.service.PaymentSubscribeService;
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
public class PaymentSubscribeController {


    private final PaymentSubscribeService paymentSubscribeService;
    private final ModelMapper modelMapper;

    @GetMapping("/v1/admin/api/subscriptions")
    public ResponseEntity<List<PaymentSubscribeResponseDTO>> getContracts(){
        List<PaymentSubscribeDTO> paymentSubscribeDTOS = paymentSubscribeService.getAllPaymentSubscribes();
        List<PaymentSubscribeResponseDTO> paymentSubscribeResponseDTOList =
                paymentSubscribeDTOS.stream()
                        .map(dto -> modelMapper.map(dto, PaymentSubscribeResponseDTO.class))
                        .toList();

        return ResponseEntity.ok(paymentSubscribeResponseDTOList);
    }

    @PostMapping("/v1/admin/api/subscriptions/search")
    public ResponseEntity<List<PaymentSubscribeResponseDTO>> searchContract(@RequestBody PaymentSubscribeSearchDTO paymentSubscribeSearchDTO){
        List<PaymentSubscribeDTO> paymentSubscribeDTOS = paymentSubscribeService.searchPaymentSubscribe(paymentSubscribeSearchDTO);
        List<PaymentSubscribeResponseDTO> paymentSubscribeResponseDTOList =
                paymentSubscribeDTOS.stream()
                        .map(dto -> modelMapper.map(dto, PaymentSubscribeResponseDTO.class))
                        .toList();
        return ResponseEntity.ok(paymentSubscribeResponseDTOList);

    }

    @PostMapping("/v1/admin/api/subscriptions/exportExcel")
    public ResponseEntity<String> exportExcel(){
        return ResponseEntity.ok(paymentSubscribeService.exportExcel());
    }
}
