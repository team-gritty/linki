package com.Gritty.Linki.domain.user.influencer.contract.UcanSign;

import com.Gritty.Linki.domain.user.influencer.requestDTO.ContractCreateRequestDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UcanSignClient {

    @Value("${uCanSignKey}")
    private String apiKey;

    private static final String API_URL = "https://app.ucansign.com/openapi/embedding/sign-creating";
    private static final String CREATE_DOCUMENT_URL = "hhttps://app.ucansign.com/openapi/templates/:documentId";
    private final OkHttpClient httpClient = new OkHttpClient();

    @Scheduled(initialDelay = 0, fixedRate = 1200000) // 20분 = 1,200,000ms
    public void sendTokenRequest() {


        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\r\n    \"apiKey\": \"" +apiKey+"\"\r\n}");
        Request request = new Request.Builder()
                .url("https://app.ucansign.com/openapi/user/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body().string());
            String token = root.path("result").path("accessToken").asText();
            TokenHolder.setToken(token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 유캔사인 템플릿으로 문서 생성 (계약서 생성)
     */
    public String createContractDocument(ContractCreateRequestDTO dto) {
        String token = TokenHolder.getToken();
        String templateId = "2342323";

        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(dto); // DTO → JSON 문자열
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"), json
            );

            Request request = new Request.Builder()
                    .url(CREATE_DOCUMENT_URL + templateId)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + token)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("x-ucansign-test", "true") // remove in production
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    JsonNode root = mapper.readTree(response.body().string());
                    return root.path("result").path("documentId").asText();
                } else {
                    throw new RuntimeException("문서 생성 실패: " + response.body().string());
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("유캔사인 문서 생성 중 오류", e);
        }
    }



}
