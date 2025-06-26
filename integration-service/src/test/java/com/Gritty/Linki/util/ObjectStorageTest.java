package com.Gritty.Linki.util;

import com.Gritty.Linki.user.common.DummyOAuth2BeansConfig;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.InputStream;

import java.io.InputStream;

@Log4j2
@SpringBootTest(properties = "uCanSignKey=dummy-test-key")
@Import(DummyOAuth2BeansConfig.class)
public class ObjectStorageTest {

    @Autowired
    private ObjectStorage objectStorage;
; // 필요 시 @BeforeEach로 초기화

        @Test
        public void testStorage() throws Exception {
            log.info("testStorage");
            // 리소스를 클래스패스에서 불러오기
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("fox.jpg");

            // MockMultipartFile로 변환
            MockMultipartFile multipartFile = new MockMultipartFile(
                    "file",               // name (폼 필드 이름)
                    "fox.jpg",            // original filename
                    "image/jpeg",         // content type
                    inputStream           // file content
            );

            // 업로드 테스트
            String result = objectStorage.uploadFile(multipartFile);
            log.info(result);
        }
    }
