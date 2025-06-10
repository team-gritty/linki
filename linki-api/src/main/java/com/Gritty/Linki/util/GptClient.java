package com.Gritty.Linki.util;

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

/**
 * GPT 클라이언트
 */
@Service
@Log4j2
public class GptClient {

    private final OkHttpClient client = new OkHttpClient();

    @Value("${openai.api-key}")
    private String apiKey;

    /**
     * GPT 요청 처리
     * @param openAiJson
     * @param msg
     * @return GPT 응닶 값
     * 파라미터로 Json 파일 + 메세지 전달
     *
     * 사용 예시 gptClient.request("GPT/Example.json","너는 누구니?")
     *
     * Json파일은 resources/GPT에 생성
     *
     * Json 파일 생성 양식
     *
     * {
     *   "model": "gpt-4o-mini",     --> GPT 모델 지정
     *   "temperature": 0.7,
     *   "top_p": 1.0,
     *   "max_tokens": 1000,        --> Max 토큰 지정(보통 800~1000이면 충분함)
     *   "messages": [
     *     {
     *       "role": "system",    --> system = 인격주입+정보주입
     *       "content": "너는 Linki라는 광고 플랫폼의 고객센터 상담사야. 친근하고 정중한 말투를 사용해. 사용자의 질문을 최대한 빠르고 정확하게 답변해줘. 어려운 말은 쓰지 말고, 예시나 비교를 자주 들어서 설명해줘. 반말은 절대 하지 마. 항상 존댓말만 사용해."
     *     },
     *
     *     {
     *       "role": "assistant",    --> assistant : 기본 응답
     *       "content": "안녕하세요! 광고 매칭 플랫폼 Linki입니다. 무엇을 도와드릴까요?"
     *     }
     *   ]
     * }
     */
    public String request(String openAiJson, String msg) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // 1. baseRequest.json 불러오기
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(openAiJson);
            if (inputStream == null) {
                throw new IllegalArgumentException("파일을 찾을 수 없습니다: " + openAiJson);
            }
            Map<String, Object> baseRequest = objectMapper.readValue(inputStream, new TypeReference<>() {});

            // 기존 messages 배열 가져오기
            List<Map<String, String>> messages = (List<Map<String, String>>) baseRequest.get("messages");

            // 유저 입력 메시지 추가
            Map<String, String> userMessage = Map.of(
                    "role", "user",
                    "content", msg
            );
            messages.add(userMessage);

            // 3. 최종 JSON 문자열 생성
            String jsonRequest = objectMapper.writeValueAsString(baseRequest);

            // 4. OkHttp로 요청
            RequestBody body = RequestBody.create(jsonRequest, MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Request failed: " + response.code());
                } else {
                    String responseBody = Objects.requireNonNull(response.body()).string();

            // 5. GPT return 값에서 답변만 가져오기
                    Map<String, Object> fullResponse = objectMapper.readValue(responseBody, new TypeReference<>() {});
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) fullResponse.get("choices");
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String content = (String) message.get("content");

                    return content;
                }
            }
        } catch (IOException e) {
            log.error("IOException occurred while processing GPT request", e);
            return "GPT 요청 중 오류가 발생했습니다.";
        }
    }

}
