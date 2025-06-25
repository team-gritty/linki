package com.Gritty.Linki.domain.user.mypage.bizNumberCheck.service;


import com.Gritty.Linki.domain.user.mypage.bizNumberCheck.DTO.RequestDTO;
import com.Gritty.Linki.domain.user.mypage.bizNumberCheck.DTO.OcrResultDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class OcrServiceImpl implements OcrService {
    @Value("${ocr-url}")
    private String apiUrl;

    @Value("${ocr-client-id}")
    String clientID;

    @Value("${ocr-key}")
    String secretKey;

    @Override
    public OcrResultDTO performOcr(RequestDTO requestDTO) throws IOException {
        MultipartFile imageFile = requestDTO.getFile();
        byte[] imageBytes = imageFile.getBytes();
        log.info("ocr확인 1 : {} , 2 {}", imageFile.getName(), imageFile.getContentType());

        OkHttpClient client = new OkHttpClient();

        // Build the message JSON
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("version", "V2");
        requestMap.put("requestId", UUID.randomUUID().toString());
        requestMap.put("timestamp", Instant.now().toEpochMilli());

        Map<String, String> imageMap = new HashMap<>();
        imageMap.put("format", "jpg");
        imageMap.put("name", "demo");

        requestMap.put("images", new Object[]{imageMap});

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonMessage = objectMapper.writeValueAsString(requestMap);

        // Build the multipart request with 'message' and 'file'
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("message", jsonMessage)
                .addFormDataPart("file", imageFile.getOriginalFilename(),
                        RequestBody.create(imageBytes, MediaType.parse("application/octet-stream")))
                .build();

        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("X-OCR-SECRET", secretKey)
                .post(requestBody)
                .build();

        log.info("ocr2");

        try (Response response = client.newCall(request).execute()) {
            log.info(response.toString());
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                log.info("OCR 응답: {}", responseBody);

                JsonNode root = objectMapper.readTree(responseBody);
                JsonNode fields = root.path("images").get(0).path("fields");

                String ocrBizNumber = null;
                String ocrCompanyName = null;

                for (JsonNode field : fields) {
                    String fieldName = field.path("name").asText();
                    String text = field.path("inferText").asText();

                    if ("bizCheck".equals(fieldName) && ocrBizNumber == null) {
                        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\\d{3}-\\d{2}-\\d{5}").matcher(text);
                        if (matcher.find()) {
                            ocrBizNumber = matcher.group();
                        }
                    }

                    if ("bizCheckname".equals(fieldName) && ocrCompanyName == null) {
                        ocrCompanyName = text;
                    }
                }

                return new OcrResultDTO(ocrBizNumber, ocrCompanyName);
            } else {
                throw new IOException("오류: " + response.code() + " - " + response.body().string());
            }
        }
    }







//    @Override
//    public String performOcr(RequestDTO requestDTO) throws IOException {
//
//        Map<String, Object> requestMap = new HashMap<>();
//        requestMap.put("version", "V2");
//        requestMap.put("requestId", UUID.randomUUID().toString());
//        requestMap.put("timestamp", Instant.now().toEpochMilli());
//
//        Map<String, String> imageMap = new HashMap<>();
//        imageMap.put("format", "jpg");
//        imageMap.put("name", "demo");
//
//        requestMap.put("images", new Object[]{imageMap});
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String jsonMessage = objectMapper.writeValueAsString(requestMap);
//        MultipartFile imageFile = requestDTO.getFile();
//
//        byte[] imageBytes = imageFile.getBytes();
//
//        RequestBody requestBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("message", jsonMessage)
//                .addFormDataPart("file", imageFile.getName(),
//                        RequestBody.create(imageBytes, MediaType.parse("application/octet-stream")))
//                .build();
//
//        Request request = new Request.Builder()
//                .url(apiUrl)
//                .addHeader("X-OCR-SECRET", secretKey)
//                .post(requestBody)
//                .build();
//
//        OkHttpClient client = new OkHttpClient();
//        try (Response response = client.newCall(request).execute()) {
//            if (response.isSuccessful() && response.body() != null) {
//                String responseBody = response.body().string();
//                JsonNode root = objectMapper.readTree(responseBody);
//                StringBuilder resultText = new StringBuilder();
//                JsonNode titleNode = root.path("images").get(0).path("title");
//                log.info("OCR 응답: {}", responseBody);
//                if (!titleNode.isMissingNode()) {
//                    String text = titleNode.path("inferText").asText();
//                    text = text.replaceAll("[^0-9]", "");
//                    resultText.append(text);
//                    log.info("OCR 응답: {}", responseBody);
//                }
//                return resultText.toString().trim();
//            } else {
//                return "오류: " + response.code() + " - " + response.body().string();
//            }
//        }
//    }
}
