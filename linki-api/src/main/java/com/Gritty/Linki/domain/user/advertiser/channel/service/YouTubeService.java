package com.Gritty.Linki.domain.user.advertiser.channel.service;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class YouTubeService {

    @Value("${youtube.api.key}")
    private String apiKey;

    private YouTube getYouTubeService() {
        return new YouTube.Builder(
                new NetHttpTransport(),
                new JacksonFactory(),
                null).setApplicationName("Linki").build();
    }

    public List<Channel> searchChannelsByKeyword(String keyword) throws IOException {
        YouTube youtube = getYouTubeService();
        List<Channel> channels = new ArrayList<>();

        try {
            // Search for channels
            YouTube.Search.List search = youtube.search().list(Arrays.asList("snippet"));
            search.setKey(apiKey);
            search.setQ(keyword);
            search.setType(Arrays.asList("channel"));
            search.setMaxResults(50L);

            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResults = searchResponse.getItems();

            if (searchResults != null) {
                // Get channel IDs
                List<String> channelIds = new ArrayList<>();
                for (SearchResult result : searchResults) {
                    channelIds.add(result.getId().getChannelId());
                }

                // Get channel details
                YouTube.Channels.List channelRequest = youtube.channels().list(Arrays.asList("snippet", "statistics"));
                channelRequest.setKey(apiKey);
                channelRequest.setId(channelIds);

                ChannelListResponse channelResponse = channelRequest.execute();
                channels.addAll(channelResponse.getItems());
            }
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        }

        return channels;
    }

    public Channel getChannelById(String channelId) throws IOException {
        YouTube youtube = getYouTubeService();

        try {
            YouTube.Channels.List channelRequest = youtube.channels().list(Arrays.asList("snippet", "statistics"));
            channelRequest.setKey(apiKey);
            channelRequest.setId(Arrays.asList(channelId));

            ChannelListResponse channelResponse = channelRequest.execute();
            List<Channel> channels = channelResponse.getItems();

            if (channels != null && !channels.isEmpty()) {
                return channels.get(0);
            }

            throw new IOException("Channel not found: " + channelId);
        } catch (GoogleJsonResponseException e) {
            throw new IOException("YouTube API error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        }
    }
}