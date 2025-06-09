package com.Gritty.Linki.domain.user.advertiser.channel.controller;

import com.Gritty.Linki.domain.user.advertiser.channel.entity.Channel;
import com.Gritty.Linki.domain.user.advertiser.channel.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    /**
     * 키워드로 유튜브 채널 10개 랜덤 검색
     * 
     * @param keyword 검색 키워드
     * @return 채널 리스트
     */
    @GetMapping("/search")
    public List<Channel> searchChannels(@RequestParam String keyword) {
        return channelService.searchChannelsByKeyword(keyword);
    }

    /**
     * 키워드로 유튜브 채널 10개를 검색해서 DB에 저장하고 반환
     * 
     * @param keyword 검색 키워드
     * @return 저장된 채널 리스트
     */
    @PostMapping("/search-and-save")
    public List<Channel> searchAndSaveChannels(@RequestParam String keyword) {
        List<Channel> channels = channelService.searchChannelsByKeyword(keyword);
        // 이미 DB에 없는 채널만 저장
        List<Channel> saved = new ArrayList<>();
        for (Channel channel : channels) {
            if (!channelService.existsByChannelId(channel.getChannelId())) {
                saved.add(channelService.saveChannel(channel));
            }
        }
        return saved;
    }
}