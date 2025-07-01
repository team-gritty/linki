package com.Gritty.Linki.domain.user.mypage.bizNumberCheck.service;

import com.Gritty.Linki.domain.user.mypage.bizNumberCheck.DTO.RequestDTO;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@Log4j2
public class NtsBizCheckServiceImpl implements NtsBizCheckService {

    @Value("${data-valid}")
    private String apiKey;

    @Override
    public boolean check(RequestDTO requestDTO) {
        // Remove all non-digit characters from bizNumber
        String bizNumber = requestDTO.getBusinessNumber().replaceAll("[^0-9]", "");

        String encodedKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8);

        OkHttpClient client = new OkHttpClient();

        String url = "https://api.odcloud.kr/api/nts-businessman/v1/status?serviceKey=" + encodedKey;

        String json = "{ \"b_no\": [\"" + bizNumber + "\"] }";

        RequestBody body = RequestBody.create(
                json,
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();

        String bStt = "";
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                // Extract b_stt value
                bStt = "";
                try {
                    int start = responseBody.indexOf("\"b_stt\":\"") + 9;
                    int end = responseBody.indexOf("\"", start);
                    bStt = responseBody.substring(start, end);
                } catch (Exception e) {
                    bStt = "파싱 실패";
                }
                log.info("사업자 상태(b_stt): " + bStt);
            } else {
                log.error("에러: " + response.code() + " - " + response.body().string());
            }
        } catch (Exception e) {
            log.error("에러: " + e.getMessage());
        }
        if (bStt.equals("계속사업자")) {
            return true;
        } else {
            return false;
        }
    }
}
