package com.ssg.paymentservice.service;

import org.springframework.stereotype.Service;

@Service
public class TossBillingService implements BillingService {

    //인증키 , userId(customerKey)
    @Override
    public String confirmBilling(String authKey, String customerKey) {
        return "";
    }
}
