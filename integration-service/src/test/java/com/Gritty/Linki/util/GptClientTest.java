package com.Gritty.Linki.util;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Log4j2
@SpringBootTest
public class GptClientTest {

    @Autowired
    private GptClient gptClient;



    @Test
    public void testGptClient() {

        String result = gptClient.request("GPT/Example.json","너는 누구니?");
        log.info(result);
    }
}
