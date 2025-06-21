package com.ssg.paymentservice.common.util;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Base64;

/**
 * 토스 빌링키 발급 요청위한 basic 인증헤더 만드는 유틸
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TossBasicAuthHeaderUtil {
    /**
     * @param secretKey 토스 시크릿 키
     * @return "Basic {base64(secretKey:)}"
     */
    public static String createBasicAuth(String secretKey) {
        String raw    =  + ":";
        String base64 = Base64.getEncoder()
                .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
        return "Basic " + base64;
    }
}
