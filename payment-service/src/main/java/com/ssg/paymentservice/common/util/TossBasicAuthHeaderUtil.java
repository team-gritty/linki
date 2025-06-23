package com.ssg.paymentservice.common.util;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Base64;

/**
 * 토스 빌링키 발급 요청위한 basic 인증헤더 만드는 유틸
 */

@Component
public class TossBasicAuthHeaderUtil {
    /**
     * @param secretKey 토스 시크릿 키
     * @return "Basic {base64(secretKey:)}"
     */
    public String createBasicAuth(String secretKey) {
        String raw    =  secretKey + ":";
        String base64 = Base64.getEncoder()
                .encodeToString(raw.getBytes());
        return "Basic " + base64;
    }
}
