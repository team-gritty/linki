package com.Gritty.Linki.util;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;

@SpringBootTest
@Log4j2
public class OcrTest {

    @Autowired
    private OcrClient ocrClient;

    @Test
    public void ocrTest() throws IOException {
        // 가짜 MultipartFile 생성
        MockMultipartFile file = new MockMultipartFile(
                "file",                  // 필드 이름
                "testImage/test.jpg",              // 원본 파일 이름
                "image/jpg",             // 컨텐츠 타입
                getClass().getResourceAsStream("/testImage/test.jpg") // 리소스 경로
        );

        InputStream is = getClass().getResourceAsStream("/testImage/test.jpg");
        if (is == null) {
            throw new IllegalArgumentException("테스트 이미지가 존재하지 않음!");
        }

        String result = ocrClient.performOcr(file);
        log.info("result={}", result);
    }
}
