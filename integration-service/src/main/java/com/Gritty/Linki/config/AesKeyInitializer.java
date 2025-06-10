package com.Gritty.Linki.config;

import com.Gritty.Linki.util.AesUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AesKeyInitializer {
    public AesKeyInitializer(@Value("${AES_KEY}") String aesKey) {
        AesUtil.setKey(aesKey);
    }
}