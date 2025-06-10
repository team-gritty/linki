package com.Gritty.Linki.util;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



@Log4j2
public class AesUtilTest {

    @BeforeEach
    public void setup() {
        AesUtil.setKey("A1c9Xe6LpQv7Tr3B"); // 테스트용 세팅
        log.info("AesUtil setup");
    }


    @Test
    public void testAes() {

        String key = "1234567890";
        String encryptKey = AesUtil.encrypt(key);
        log.info("key: " + key);
        log.info("encrypted key: " + encryptKey);

        String decryptKey = AesUtil.decrypt(encryptKey);
        log.info("decrypted key: " + decryptKey);
    }



}
