package com.Gritty.Linki.domain.user.influencer.contract.UcanSign;

import com.Gritty.Linki.domain.user.influencer.dto.UcanCreateDTO;
import com.Gritty.Linki.domain.user.influencer.requestDTO.ContractCreateRequestDTO;
import com.Gritty.Linki.domain.user.influencer.requestDTO.ContractSignRequestDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Log4j2
public class UcanSignClient {

    @Value("${uCanSignKey}")
    private String apiKey;

    private final ObjectMapper objectMapper;

    private static final String templateId = "1937413181262622722";
    private static final String API_URL = "https://app.ucansign.com/openapi/embedding/sign-creating";
    private static final String CREATE_DOCUMENT_BASE_URL = "https://app.ucansign.com/openapi/templates/";
    private final OkHttpClient httpClient = new OkHttpClient();

    @Scheduled(initialDelay = 0, fixedRate = 1200000) // 20분 = 1,200,000ms
    public void sendTokenRequest() {


        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        try {
            String json = objectMapper.writeValueAsString(Map.of("apiKey", apiKey));
            RequestBody body = RequestBody.create(json, mediaType);
            log.info("apiKey: " + apiKey);
            log.info("요청 바디 내용: {}", json);
            Request request = new Request.Builder()
                    .url("https://app.ucansign.com/openapi/user/token")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.body().string());
                String token = root.path("result").path("accessToken").asText();
                log.info("토큰 발급받음 : {}", token);
                TokenHolder.setToken(token);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 유캔사인 템플릿으로 문서 생성 (계약서 생성)
     */
    public String createContractDocument(UcanCreateDTO dto) {
        String token = TokenHolder.getToken();

        try {
            // Build JSON dynamically using Map and DTO
            Map<String, Object> jsonMap = new java.util.LinkedHashMap<>();
            jsonMap.put("documentName", "광고 계약서");
            jsonMap.put("processType", "PROCEDURE");
            jsonMap.put("isSequential", true);
            jsonMap.put("isSendMessage", true);
            jsonMap.put("redirectUrl", "https://www.gritty.com/");

            java.util.List<Map<String, Object>> participants = new java.util.ArrayList<>();
            Map<String, Object> participant1 = new java.util.LinkedHashMap<>();
            participant1.put("name", dto.getParticipant1Name());
            participant1.put("signingMethodType", "email");
            participant1.put("signingContactInfo", dto.getParticipant1Email());
            participant1.put("signingOrder", 1);
            participants.add(participant1);

            Map<String, Object> participant2 = new java.util.LinkedHashMap<>();
            participant2.put("name", dto.getParticipant2Name());
            participant2.put("signingMethodType", "email");
            participant2.put("signingContactInfo", dto.getParticipant2Email());
            participant2.put("signingOrder", 2);
            participants.add(participant2);

            jsonMap.put("participants", participants);

            java.util.List<Map<String, Object>> fields = new java.util.ArrayList<>();
            fields.add(java.util.Map.of("fieldName", "advertiserName", "value", dto.getAdvertiserName()));
            fields.add(java.util.Map.of("fieldName", "influencerName", "value", dto.getInfluencerName()));
            fields.add(java.util.Map.of("fieldName", "contractStartDateYear", "value", dto.getContractStartDateYear()));
            fields.add(java.util.Map.of("fieldName", "contractStartDateMonth", "value", dto.getContractStartDateMonth()));
            fields.add(java.util.Map.of("fieldName", "contractStartDateDay", "value", dto.getContractStartDateDay()));
            fields.add(java.util.Map.of("fieldName", "contractEndDateYear", "value", dto.getContractEndDateYear()));
            fields.add(java.util.Map.of("fieldName", "contractEndDateMonth", "value", dto.getContractEndDateMonth()));
            fields.add(java.util.Map.of("fieldName", "contractEndDateDay", "value", dto.getContractEndDateDay()));
            fields.add(java.util.Map.of("fieldName", "WrittenDateYear", "value", dto.getWrittenDateYear()));
            fields.add(java.util.Map.of("fieldName", "writtenDateMonth", "value", dto.getWrittenDateMonth()));
            fields.add(java.util.Map.of("fieldName", "writtenDateDay", "value", dto.getWrittenDateDay()));
            fields.add(java.util.Map.of("fieldName", "businessNumber", "value", dto.getBusinessNumber()));
            fields.add(java.util.Map.of("fieldName", "advertiserAddress", "value", dto.getAdvertiserAddress()));
            fields.add(java.util.Map.of("fieldName", "influencerAddress", "value", dto.getInfluencerAddress()));
            fields.add(java.util.Map.of("fieldName", "adDeliveryUrl", "value", dto.getAdDeliveryUrl()));

            // TODO : 링크 변환 넣어야함
            String url = "";

            String special = "광고 할 상품 Link : " + url + "\n" + dto.getContractSpecialTerms();

            fields.add(java.util.Map.of("fieldName", "contractSpecialTerms", "value", special));

            jsonMap.put("fields", fields);
            jsonMap.put("customValue", dto.getCustomValue());

            String json = objectMapper.writeValueAsString(jsonMap);
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"), json
            );
            log.info("유캔싸인 요청 바디 : {}", body);
            log.info("json : {}", json);
            log.info("token : {}", token);

            Request request = new Request.Builder()
                    .url(CREATE_DOCUMENT_BASE_URL + templateId)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + token)
                    .addHeader("Content-Type", "application/json")
//                    .addHeader("x-ucansign-test", "true") // remove in production
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    JsonNode root = objectMapper.readTree(response.body().string());
                    log.info("유캔싸인 호출 결과 : {}", root);
                    return root.path("result").path("documentId").asText();
                } else {
                    throw new RuntimeException("문서 생성 실패: " + response.body().string());
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("유캔사인 문서 생성 중 오류", e);
        }
    }






    public String getDocument(String documentId) {
        String token = TokenHolder.getToken();
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        if (documentId == null || documentId.isBlank()) {
            throw new IllegalArgumentException("documentId가 비어 있습니다");
        }

        MediaType mediaType = MediaType.parse("text/plain");
        Request request = new Request.Builder()
                .url("https://app.ucansign.com/openapi/documents/" + documentId + "/file")
                .get()
                .addHeader("Authorization", "Bearer " + token)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                throw new RuntimeException("UCanSign 문서 파일 조회 실패: " + response.code() + "\n" + response.body().string());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
