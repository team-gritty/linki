package com.ssg.subscribeservice.util;

import com.ssg.subscribeservice.subsenum.SubscribeCode;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Component
public class SubscribeCodeConverterImpl implements SubscribeCodeConverter {

    private final Map<String, Supplier<SubscribeCode>> converterMap = new HashMap<>();


    public SubscribeCode toSubscribeCode(String role) {
        converterMap.put("ROLE_INFLUENCER", () -> SubscribeCode.InfSub);
        converterMap.put("ROLE_ADVERTISER", () -> SubscribeCode.adSub);
        return converterMap
                .getOrDefault(role, () -> {
                    throw new IllegalArgumentException("지원하지 않는 권한입니다: " + role);
                })
                .get();
    }
}
