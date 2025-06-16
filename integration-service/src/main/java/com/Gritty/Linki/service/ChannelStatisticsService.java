package com.Gritty.Linki.service;

import com.Gritty.Linki.entity.CollectedChannel;
import com.Gritty.Linki.entity.SubscriberHistory;
import com.Gritty.Linki.repository.SubscriberHistoryRepository;
import com.Gritty.Linki.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChannelStatisticsService {

    private final SubscriberHistoryRepository subscriberHistoryRepository;

    @Transactional
    public SubscriberHistory createSubscriberSnapshot(CollectedChannel channel) {
        SubscriberHistory history = new SubscriberHistory();
        history.setHistoryId(IdGenerator.subscriberHistoryId());
        history.setCollectedChannel(channel);
        history.setSubscriberCount(channel.getSubscriberCount() != null ? channel.getSubscriberCount() : 0L);
        history.setVideoCount(channel.getVideoCount() != null ? channel.getVideoCount() : 0);
        history.setViewCount(channel.getViewCount() != null ? channel.getViewCount() : 0L);
        history.setSnapshotDate(LocalDateTime.now());
        return subscriberHistoryRepository.save(history);
    }

    public double calculateAverageViewsPerVideo(CollectedChannel channel) {
        return channel.getVideoCount() != null && channel.getVideoCount() > 0
                ? (double) channel.getViewCount() / channel.getVideoCount()
                : 0.0;
    }

    public double calculateViewsToSubscriberRatio(CollectedChannel channel) {
        return channel.getSubscriberCount() != null && channel.getSubscriberCount() > 0
                ? calculateAverageViewsPerVideo(channel) / channel.getSubscriberCount()
                : 0.0;
    }

    public double calculateAverageLikesPerVideo(CollectedChannel channel) {
        return channel.getVideoCount() != null && channel.getVideoCount() > 0 && channel.getLikeCount() != null
                ? (double) channel.getLikeCount() / channel.getVideoCount()
                : 0.0;
    }

    public double calculateAverageCommentsPerVideo(CollectedChannel channel) {
        return channel.getVideoCount() != null && channel.getVideoCount() > 0 && channel.getCommentCount() != null
                ? (double) channel.getCommentCount() / channel.getVideoCount()
                : 0.0;
    }

    public double calculateLikeToViewRatio(CollectedChannel channel) {
        return channel.getViewCount() != null && channel.getViewCount() > 0 && channel.getLikeCount() != null
                ? (double) channel.getLikeCount() / channel.getViewCount()
                : 0.0;
    }

    public double calculateCommentToViewRatio(CollectedChannel channel) {
        return channel.getViewCount() != null && channel.getViewCount() > 0 && channel.getCommentCount() != null
                ? (double) channel.getCommentCount() / channel.getViewCount()
                : 0.0;
    }
}