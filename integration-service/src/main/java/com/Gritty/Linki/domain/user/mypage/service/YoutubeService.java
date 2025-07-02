package com.Gritty.Linki.domain.user.mypage.service;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.account.account.repository.AccountRepository;
import com.Gritty.Linki.domain.user.mypage.dto.YoutubeApiResponse;
import com.Gritty.Linki.domain.user.mypage.dto.YoutubeChannelInfo;
import com.Gritty.Linki.entity.Channel;
import com.Gritty.Linki.entity.Influencer;
import com.Gritty.Linki.entity.User;
import com.Gritty.Linki.repository.ChannelRepository;
import com.Gritty.Linki.repository.InfluencerRepository;
import com.Gritty.Linki.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class YoutubeService {
    @Value("${GOOGLE_CLIENT_ID}")
    private String clientId;

    @Value("${GOOGLE_CLIENT_SECRET}")
    private String clientSecret;

    @Value("${GOOGLE_REDIRECTURI}")
    private String redirectUri;

    private final RestTemplate restTemplate = new RestTemplate();
    private final InfluencerRepository influencerRepository;
    private final ChannelRepository channelRepository;
    private final AccountRepository accountRepository;


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
        String url = "https://www.googleapis.com/youtube/v3/channels?part=snippet,brandingSettings,contentDetails,statistics,status&mine=true";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<YoutubeApiResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, YoutubeApiResponse.class);
        return response.getBody().getItems().get(0); // 첫 번째 채널 정보 리턴
    }

    @Transactional
    public String registerChannel(CustomUserDetails userDetails, YoutubeChannelInfo channelInfo) {
        String influencerId = IdGenerator.influencerId();

        Influencer influencer = Influencer.builder()
                .influencerId(influencerId)
                .userId(userDetails.getUserId())
                .build();

        influencerRepository.save(influencer);

        User user = accountRepository.findById(userDetails.getUserId()).orElseThrow();
        user.setUserRole("ROLE_INFLUENCER");

        String channelId = IdGenerator.channelId();
        Channel channel = Channel.builder()
                .channelId(channelId)
                .youtubeChannelId(channelInfo.getId())
                .channelName(channelInfo.getTitle())
                .channelUrl("http://www.youtube.com/channel/" + channelInfo.getId())
                .channelCountry(channelInfo.getCountry())
                .channelCategory(channelInfo.getCategory())
                .channelCreatedAt(LocalDateTime.parse(channelInfo.getPublishedAt()))
                .channelDescription(channelInfo.getDescription())
                .channelThumbnailUrl(channelInfo.getThumbnailUrl())
                .influencer(influencer)
                .build();

        log.info("채널아이디" + channelInfo.getId());
        channelRepository.save(channel);

        return channelId;

    }

}
