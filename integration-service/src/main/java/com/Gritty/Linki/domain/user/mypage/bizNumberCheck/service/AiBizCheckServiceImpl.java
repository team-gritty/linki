package com.Gritty.Linki.domain.user.mypage.bizNumberCheck.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Log4j2
public class AiBizCheckServiceImpl implements AiBizCheckService {

    private final OkHttpClient client = new OkHttpClient();
    @Value("${openai.api-key}")
    private String apiKey;

    @Override
    public String check(String openAiJson, String msg) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        log.info("check시작");
        // 1. baseRequest.json 불러오기
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("bizNumber/bizNumber.json");
        if (inputStream == null) {
            throw new IllegalArgumentException("파일을 찾을 수 없습니다: " + openAiJson);
        }
        log.info("check시작1");
        Map<String, Object> baseRequest = objectMapper.readValue(inputStream, new TypeReference<>() {});
        log.info("check시작2");
// 기존 messages 배열 가져오기
        List<Map<String, String>> messages = (List<Map<String, String>>) baseRequest.get("messages");
        log.info("check시작3");
// 유저 입력 메시지 추가
        Map<String, String> userMessage = Map.of(
                "role", "user",
                "content", msg
        );
        log.info("check시작4");
        messages.add(userMessage);

// 3. 최종 JSON 문자열 생성
        String jsonRequest = objectMapper.writeValueAsString(baseRequest);
        log.info("check시작5");
// 4. OkHttp로 요청
        RequestBody body = RequestBody.create(jsonRequest, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization","Bearer " + apiKey)
                .build();
        log.info("API키: {}", apiKey);
        log.info("check시작6");
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.info("check시작6+");
                throw new IOException("Request failed: " + response.code());
            } else {
                String responseBody = Objects.requireNonNull(response.body()).string();
                log.info("check시작7");
//                log.info("GPT 전체 답변");
//                log.info(responseBody);

                // content 필드만 파싱
//                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> fullResponse = objectMapper.readValue(responseBody, new TypeReference<>() {});
                List<Map<String, Object>> choices = (List<Map<String, Object>>) fullResponse.get("choices");
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                String content = (String) message.get("content");


                log.info("질문");
                log.info(msg);
                log.info("GPT content");
                log.info(content);
                return content;
            }
        }
    }
}
