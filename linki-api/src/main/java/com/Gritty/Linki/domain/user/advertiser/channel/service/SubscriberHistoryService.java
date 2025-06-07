package com.Gritty.Linki.domain.user.advertiser.channel.service;

import com.Gritty.Linki.domain.user.advertiser.channel.entity.Channel;
import com.Gritty.Linki.domain.user.advertiser.channel.entity.SubscriberHistory;
import com.Gritty.Linki.domain.user.advertiser.channel.repository.ChannelRepository;
import com.Gritty.Linki.domain.user.advertiser.channel.repository.SubscriberHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriberHistoryService {

    private final ChannelRepository channelRepository;
    private final SubscriberHistoryRepository subscriberHistoryRepository;
    private final YouTubeService youTubeService;

    @Transactional
    public void collectSubscriberCount(Channel channel) {
        try {
            com.google.api.services.youtube.model.Channel youtubeChannel = youTubeService
                    .getChannelById(channel.getChannelId());

            Long subscriberCount = youtubeChannel.getStatistics().getSubscriberCount().longValue();

            SubscriberHistory history = SubscriberHistory.builder()
                    .channel(channel)
                    .subscriberCount(subscriberCount)
                    .build();

            subscriberHistoryRepository.save(history);

            // Update channel's current subscriber count
            channel.setSubscriberCount(subscriberCount);
            channelRepository.save(channel);
        } catch (Exception e) {
            // Log error but don't throw to prevent scheduler from stopping
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 0 */6 * * *") // Every 6 hours
    @Transactional
    public void collectAllChannelsSubscriberCount() {
        List<Channel> channels = channelRepository.findAll();
        for (Channel channel : channels) {
            collectSubscriberCount(channel);
        }
    }

    public List<SubscriberHistory> getChannelHistory(Long channelId) {
        return subscriberHistoryRepository.findByChannelIdOrderByCollectedAtDesc(channelId);
    }

    public List<SubscriberHistory> getChannelHistoryBetween(Long channelId, LocalDateTime startDate,
            LocalDateTime endDate) {
        return subscriberHistoryRepository.findByChannelIdAndCollectedAtBetweenOrderByCollectedAtDesc(
                channelId, startDate, endDate);
    }
}