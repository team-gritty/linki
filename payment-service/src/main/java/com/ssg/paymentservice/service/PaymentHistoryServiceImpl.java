package com.ssg.paymentservice.service;

import com.ssg.paymentservice.repository.PaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentHistoryServiceImpl implements PaymentHistoryService {

    private final PaymentHistoryRepository paymentHistoryRepository;

    @Override
    public Integer countByUserIdAndSuccessTrue(String userId){
        return paymentHistoryRepository.countByUserIdAndSuccessTrue(userId);
    }
}
