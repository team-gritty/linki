package com.Gritty.Linki.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 네이버 클라우드 OCR API와 연동하여 이미지에서 숫자(예: 사업자등록번호)를 추출하는 유틸리티 클래스입니다.
 *
 * MultipartFile로 전달된 이미지를 OCR API에 전송하고, 응답 결과에서 특정 텍스트를 추출합니다.
 */
@Service
public class OcrClient {

    @Value("${ocr.url}")
    String apiUrl;

    @Value("${ocr.key}")
    String secretKey;

    /**
     * OCR API를 호출하여 업로드된 이미지에서 숫자를 추출합니다.
     *
     * 요청 본문은 JSON과 이미지 바이너리를 포함하는 멀티파트 형식으로 구성되며,
     * 네이버 클라우드 OCR(Open API)을 대상으로 요청이 전송됩니다.
     *
     * 응답 데이터 중 title 항목의 inferText 값을 찾아 숫자만 추출해 반환합니다.
     *
     * @param imgPath Multipart 형식의 업로드 이미지 파일 (jpg 형식 권장)
     * @return OCR 추출 결과 숫자 문자열(사업자 번호), 또는 오류 메시지
     */
    public String performOcr(MultipartFile imgPath) {
        try {
            // OCR API 요청을 위한 기본 request 정보 세팅
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("version", "V2");
            requestMap.put("requestId", UUID.randomUUID().toString());
            requestMap.put("timestamp", Instant.now().toEpochMilli());

            // 이미지 정보 세팅 (실제 이미지 바이트는 나중에 첨부됨)
            Map<String, String> imageMap = new HashMap<>();
            imageMap.put("format", "jpg");
            imageMap.put("name", "demo");

            requestMap.put("images", new Object[]{imageMap});

            // JSON 메시지 직렬화 및 이미지 바이트 추출
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonMessage = objectMapper.writeValueAsString(requestMap);
            byte[] imageBytes = imgPath.getBytes();

            // 멀티파트 요청 본문 구성
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("message", jsonMessage)
                    .addFormDataPart("file", imgPath.getName(),
                            RequestBody.create(imageBytes, MediaType.parse("application/octet-stream")))
                    .build();

            // HTTP 요청 생성 및 전송
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .addHeader("X-OCR-SECRET", secretKey)
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            try (Response response = client.newCall(request).execute()) {
                // 응답 성공 시 결과 파싱
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    JsonNode root = objectMapper.readTree(responseBody);

                    // OCR 결과 중 숫자만 추출하여 반환
                    StringBuilder resultText = new StringBuilder();
                    JsonNode titleNode = root.path("images").get(0).path("title");
                    if (!titleNode.isMissingNode()) {
                        String text = titleNode.path("inferText").asText();
                        text = text.replaceAll("[^0-9]", "");
                        resultText.append(text);
                    }
                    return resultText.toString().trim();
                } else {
                    // 실패 시 상태 코드 + 메시지 반환
                    return "오류: " + response.code() + " - " + response.body().string();
                }
            }
        } catch (IOException e) {
            return "오류: OCR 요청 중 예외 발생 - " + e.getMessage();
        }
    }

}
