package com.ssg.paymentservice.common.toss;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TossConfig {
    @Value("${toss.api.secretKey}")
    private String secretKey;

    public String getSecretKey() {
        return secretKey;
    }
}
