package com.linki.admin_integration_service.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSubscribeKeysetResponseDTO {
    
    private List<PaymentSubscribeResponseDTO> list;
    private boolean hasNext;
    private int size;
    private String nextCursor;
    private String currentCursor;
    private int requestedSize;
    
} 