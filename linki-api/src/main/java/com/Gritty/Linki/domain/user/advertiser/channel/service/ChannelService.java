package com.Gritty.Linki.domain.user.advertiser.channel.service;

import com.Gritty.Linki.domain.user.advertiser.channel.entity.Channel;
import com.Gritty.Linki.domain.user.advertiser.channel.repository.ChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final YouTubeService youTubeService;

    @Transactional
    public List<Channel> searchAndSaveChannels(String keyword) throws Exception {
        List<com.google.api.services.youtube.model.Channel> youtubeChannels = youTubeService
                .searchChannelsByKeyword(keyword);

        return youtubeChannels.stream()
                .map(this::convertToChannelEntity)
                .map(this::saveOrUpdateChannel)
                .collect(Collectors.toList());
    }

    private Channel convertToChannelEntity(com.google.api.services.youtube.model.Channel youtubeChannel) {
        return Channel.builder()
                .channelId(youtubeChannel.getId())
                .title(youtubeChannel.getSnippet().getTitle())
                .description(youtubeChannel.getSnippet().getDescription())
                .thumbnailUrl(youtubeChannel.getSnippet().getThumbnails().getDefault().getUrl())
                .subscriberCount(youtubeChannel.getStatistics().getSubscriberCount().longValue())
                .videoCount(youtubeChannel.getStatistics().getVideoCount().longValue())
                .viewCount(youtubeChannel.getStatistics().getViewCount().longValue())
                .build();
    }

    private Channel saveOrUpdateChannel(Channel channel) {
        return channelRepository.findByChannelId(channel.getChannelId())
                .map(existingChannel -> {
                    existingChannel.setTitle(channel.getTitle());
                    existingChannel.setDescription(channel.getDescription());
                    existingChannel.setThumbnailUrl(channel.getThumbnailUrl());
                    existingChannel.setSubscriberCount(channel.getSubscriberCount());
                    existingChannel.setVideoCount(channel.getVideoCount());
                    existingChannel.setViewCount(channel.getViewCount());
                    return channelRepository.save(existingChannel);
                })
                .orElseGet(() -> channelRepository.save(channel));
    }
}