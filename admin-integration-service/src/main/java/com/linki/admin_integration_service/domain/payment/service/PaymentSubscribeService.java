package com.linki.admin_integration_service.domain.payment.service;

import com.linki.admin_integration_service.domain.payment.dto.PaymentSubscribeDTO;
import com.linki.admin_integration_service.domain.payment.dto.PaymentSubscribeKeysetResponseDTO;
import com.linki.admin_integration_service.domain.payment.dto.PaymentSubscribeSearchDTO;

import java.util.List;

public interface PaymentSubscribeService {
    List<PaymentSubscribeDTO> getAllPaymentSubscribes();
    List<PaymentSubscribeDTO> searchPaymentSubscribe(PaymentSubscribeSearchDTO searchDTO);
    String exportExcel();


    PaymentSubscribeKeysetResponseDTO getAllPaymentSubscribesWithKeyset(String cursor, int size);
    PaymentSubscribeKeysetResponseDTO searchPaymentSubscribeWithKeyset(PaymentSubscribeSearchDTO searchDTO, String cursor, int size);
}

