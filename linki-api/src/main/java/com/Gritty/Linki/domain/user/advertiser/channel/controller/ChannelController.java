package com.Gritty.Linki.domain.user.advertiser.channel.controller;

import com.Gritty.Linki.domain.user.advertiser.channel.entity.Channel;
import com.Gritty.Linki.domain.user.advertiser.channel.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/advertiser/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @GetMapping("/search")
    public ResponseEntity<List<Channel>> searchChannels(@RequestParam String keyword) {
        try {
            List<Channel> channels = channelService.searchAndSaveChannels(keyword);
            return ResponseEntity.ok(channels);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}