package com.Gritty.Linki.domain.user.mypage.service;

import com.Gritty.Linki.domain.user.mypage.dto.YoutubeApiResponse;
import com.Gritty.Linki.domain.user.mypage.dto.YoutubeChannelInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class YoutubeService {
    @Value("${GOOGLE_CLIENT_ID}")
    private String clientId;

    @Value("${GOOGLE_CLIENT_SECRET}")
    private String clientSecret;

    @Value("http://localhost:8081/v1/api/youtube/callback")
    private String redirectUri;

    private final RestTemplate restTemplate = new RestTemplate();

    public String exchangeCodeForAccessToken(String code) {
        String tokenUrl = "https://oauth2.googleapis.com/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", clientId); // application.yml에 설정
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
        return (String) response.getBody().get("access_token");
    }

    public YoutubeChannelInfo getMyChannel(String accessToken) {
        String url = "https://www.googleapis.com/youtube/v3/channels?part=snippet&mine=true";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<YoutubeApiResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, YoutubeApiResponse.class
        );

        return response.getBody().getItems().get(0); // 첫 번째 채널 정보 리턴
    }


}
