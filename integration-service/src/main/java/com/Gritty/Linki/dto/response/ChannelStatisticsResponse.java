package com.Gritty.Linki.dto.response;

import com.Gritty.Linki.entity.CollectedChannel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChannelStatisticsResponse {
    private double averageViewsPerVideo;
    private double viewsToSubscriberRatio;
    private double averageLikesPerVideo;
    private double averageCommentsPerVideo;
    private double likeToViewRatio;
    private double commentToViewRatio;

    public static ChannelStatisticsResponse from(CollectedChannel channel) {
        return ChannelStatisticsResponse.builder()
                .averageViewsPerVideo(calculateAverageViewsPerVideo(channel))
                .viewsToSubscriberRatio(calculateViewsToSubscriberRatio(channel))
                .averageLikesPerVideo(calculateAverageLikesPerVideo(channel))
                .averageCommentsPerVideo(calculateAverageCommentsPerVideo(channel))
                .likeToViewRatio(calculateLikeToViewRatio(channel))
                .commentToViewRatio(calculateCommentToViewRatio(channel))
                .build();
    }

    private static double calculateAverageViewsPerVideo(CollectedChannel channel) {
        return channel.getVideoCount() != null && channel.getVideoCount() > 0
                ? (double) channel.getViewCount() / channel.getVideoCount()
                : 0.0;
    }

    private static double calculateViewsToSubscriberRatio(CollectedChannel channel) {
        return channel.getSubscriberCount() != null && channel.getSubscriberCount() > 0
                ? calculateAverageViewsPerVideo(channel) / channel.getSubscriberCount()
                : 0.0;
    }

    private static double calculateAverageLikesPerVideo(CollectedChannel channel) {
        return channel.getVideoCount() != null && channel.getVideoCount() > 0 && channel.getLikeCount() != null
                ? (double) channel.getLikeCount() / channel.getVideoCount()
                : 0.0;
    }

    private static double calculateAverageCommentsPerVideo(CollectedChannel channel) {
        return channel.getVideoCount() != null && channel.getVideoCount() > 0 && channel.getCommentCount() != null
                ? (double) channel.getCommentCount() / channel.getVideoCount()
                : 0.0;
    }

    private static double calculateLikeToViewRatio(CollectedChannel channel) {
        return channel.getViewCount() != null && channel.getViewCount() > 0 && channel.getLikeCount() != null
                ? (double) channel.getLikeCount() / channel.getViewCount()
                : 0.0;
    }

    private static double calculateCommentToViewRatio(CollectedChannel channel) {
        return channel.getViewCount() != null && channel.getViewCount() > 0 && channel.getCommentCount() != null
                ? (double) channel.getCommentCount() / channel.getViewCount()
                : 0.0;
    }
}